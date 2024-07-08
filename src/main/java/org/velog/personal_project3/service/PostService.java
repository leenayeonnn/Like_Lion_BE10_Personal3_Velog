package org.velog.personal_project3.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.Post;
import org.velog.personal_project3.domain.User;
import org.velog.personal_project3.repository.BlogRepository;
import org.velog.personal_project3.repository.PostRepository;
import org.velog.personal_project3.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final UserRepository userRepository;
	private final BlogRepository blogRepository;
	private final PostRepository postRepository;

	public void savePost(Post post, UserDetails userDetails) {
		post.setRegistrationDate(LocalDateTime.now());

		User user = userRepository.findByUsername(userDetails.getUsername());
		post.setBlog(blogRepository.findByUser(user));

		postRepository.save(post);
	}

	public Page<Post> findAllPosts(Pageable pageable) {
		return postRepository.findAll(pageable);
	}

	public Post findByBlogAndTitle(Blog blog, String title) {
		return postRepository.findByBlogAndTitle(blog, title);
	}
}
