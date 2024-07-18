package org.velog.personal_project3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.User;
import org.velog.personal_project3.repository.BlogRepository;
import org.velog.personal_project3.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogService {
	private final BlogRepository blogRepository;
	private final UserRepository userRepository;

	@Transactional(readOnly = false)
	public void makeBlog(User user) {
		blogRepository.save(new Blog(user));
	}

	public void editBlog(Blog blog) {
		blogRepository.save(blog);
	}

	public Blog findByUser(User byUsername) {
		return blogRepository.findByUser(byUsername);
	}

	public Blog findById(Long id) {
		return blogRepository.findById(id).orElse(null);
	}

	public Blog findByUsername(String username) {
		return blogRepository.findByUser(userRepository.findByUsername(username));
	}
}
