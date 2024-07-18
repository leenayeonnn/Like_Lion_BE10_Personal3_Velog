package org.velog.personal_project3.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.velog.personal_project3.domain.Comment;
import org.velog.personal_project3.domain.Post;
import org.velog.personal_project3.repository.CommentRepository;
import org.velog.personal_project3.repository.PostRepository;
import org.velog.personal_project3.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;

	public Page<Comment> findByPostId(Long postId, Pageable pageable) {
		Post post = postRepository.findById(postId).orElse(null);
		return commentRepository.findByPostAndParentId(post, 0L, pageable);
	}

	public void saveComment(Long postId, String content, String username, Long parentId) {
		Comment comment = new Comment();
		comment.setPost(postRepository.findById(postId).orElse(null));
		comment.setContent(content);
		comment.setParentId(parentId);
		comment.setUserId(userRepository.findByUsername(username).getId());
		comment.setRegistrationDate(LocalDateTime.now());

		commentRepository.save(comment);
	}

	public Comment findById(Long id) {
		return commentRepository.findById(id).orElse(null);
	}

	public void editComment(Comment comment) {
		commentRepository.save(comment);
	}

	public void delete(Comment comment) {
		commentRepository.delete(comment);
	}

	public List<Comment> findSubByParentId(Long parentId) {
		return commentRepository.findByParentIdOrderByRegistrationDate(parentId);
	}
}
