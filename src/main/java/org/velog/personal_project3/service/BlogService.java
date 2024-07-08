package org.velog.personal_project3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.User;
import org.velog.personal_project3.repository.BlogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogService {
	private final BlogRepository blogRepository;

	@Transactional(readOnly = false)
	public void makeBlog(User user) {
		blogRepository.save(new Blog(user));
	}

	public Blog findByUser(User byUsername) {
		return blogRepository.findByUser(byUsername);
	}
}
