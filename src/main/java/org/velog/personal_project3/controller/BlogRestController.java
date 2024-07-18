package org.velog.personal_project3.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.service.BlogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogRestController {
	private final BlogService blogService;

	@GetMapping
	public ResponseEntity<?> getBlogInfo(@RequestParam("username") String username) {
		Blog blog = blogService.findByUsername(username);
		if(blog == null) {
			return ResponseEntity.badRequest().body("없는 블로그 입니다.");
		}

		Map<String, Object> response = new HashMap<>();
		response.put("blogId", blog.getId());
		response.put("profileUrl", blog.getUser().getProfileUrl());
		response.put("blogName", blog.getName());
		response.put("blogDescription", blog.getDescription());

		return ResponseEntity.ok(response);
	}

	@PutMapping("/description")
	public ResponseEntity<?> editBlogDescription(@RequestParam("username") String username, @RequestParam("description") String description) {
		Blog blog = blogService.findByUsername(username);

		if(blog == null) {
			return ResponseEntity.badRequest().body("없는 블로그 입니다.");
		}

		blog.setDescription(description.trim());
		blogService.editBlog(blog);

		Map<String, Object> response = new HashMap<>();
		response.put("description", blog.getDescription());

		return ResponseEntity.ok(response);
	}
}
