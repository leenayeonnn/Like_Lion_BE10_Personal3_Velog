package org.velog.personal_project3.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.velog.personal_project3.domain.Post;
import org.velog.personal_project3.domain.Series;
import org.velog.personal_project3.domain.Tag;
import org.velog.personal_project3.dto.WritePostDto;
import org.velog.personal_project3.repository.BlogRepository;
import org.velog.personal_project3.repository.PostRepository;
import org.velog.personal_project3.repository.SeriesRepository;
import org.velog.personal_project3.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final BlogRepository blogRepository;
	private final PostRepository postRepository;
	private final TagRepository tagRepository;
	private final SeriesRepository seriesRepository;

	public void save(WritePostDto writePostDto, UserDetails userDetails) {
		Long postId = writePostDto.getPostId();

		Post post = postId != null ? postRepository.findById(postId).get() : new Post();

		if (post.getRegistrationDate() == null) {
			post.setRegistrationDate(LocalDateTime.now());
			post.setBlog(blogRepository.findById(writePostDto.getBlogId()).get());
			post.setView(0L);
		} else if (post.getIsPublished() == !(writePostDto.getIsPublished())) {
			post.setRegistrationDate(LocalDateTime.now());
			post.setView(0L);
		}

		post.setTitle(writePostDto.getTitle());
		post.setContent(writePostDto.getContent());
		post.setIsPublic(writePostDto.getIsPublic());
		post.setIsPublished(writePostDto.getIsPublished());
		post.setMainImgUrl(writePostDto.getMainImgUrl());

		String tagList = writePostDto.getTagList();
		if (tagList != null && !tagList.isEmpty()) {
			Set<Tag> tagSet = new HashSet<>();
			StringTokenizer st = new StringTokenizer(tagList);
			while (st.hasMoreTokens()) {
				String tagName = st.nextToken();
				System.out.println(tagName);
				Tag tag;
				if ((tag = tagRepository.findByName(tagName)) != null) {
					tagSet.add(tag);
				} else {
					tag = new Tag(tagName);
					tagRepository.save(tag);
					tagSet.add(tag);
				}
			}
			post.setTags(tagSet);
		}

		post.setSeries(seriesRepository.findByBlogIdAndName(writePostDto.getBlogId(), writePostDto.getSeries()));

		postRepository.save(post);
	}

	public List<Post> findByIsPublicAndIsPublished(Pageable pageable, boolean isPublic, boolean isPublished) {
		return postRepository.findByIsPublicAndIsPublished(pageable, isPublic, isPublished);
	}

	public Post findById(Long id) {
		return postRepository.findById(id).orElse(null);
	}

	public List<Post> findSavedPostByBlogId(Long blogId) {
		return postRepository.findByBlogIdAndIsPublishedOrderByRegistrationDateDesc(blogId, false);
	}

	public void deleteById(Long id) {
		postRepository.deleteById((id));
	}

	public List<Post> findBlogPostByBlogId(Pageable pageable, Long blogId, boolean isOwner) {

		return isOwner ? postRepository.findByBlogIdAndIsPublishedOrderByRegistrationDateDesc(blogId, true, pageable) :
			postRepository.findByBlogIdAndIsPublishedAndIsPublicOrderByRegistrationDateDesc(blogId, true, true,
				pageable);
	}

	public List<Post> findBySeries(Series series, boolean isOwner, Pageable pageable) {
		return isOwner ? postRepository.findBySeriesAndIsPublished(series, true, pageable) :
			postRepository.findBySeriesAndIsPublishedAndIsPublic(series, true, true, pageable);
	}

	public void addView(Long id) {
		Post post = postRepository.findById(id).orElse(null);
		if (post != null) {
			post.setView(post.getView() + 1);
			postRepository.save(post);
		}
	}
}
