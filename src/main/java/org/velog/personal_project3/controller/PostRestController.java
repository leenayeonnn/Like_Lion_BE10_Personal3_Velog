package org.velog.personal_project3.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.Post;
import org.velog.personal_project3.domain.Series;
import org.velog.personal_project3.service.BlogService;
import org.velog.personal_project3.service.PostService;
import org.velog.personal_project3.service.SeriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

	private final PostService postService;
	private final BlogService blogService;
	private final SeriesService seriesService;

	@PostMapping("/image")
	public ResponseEntity<?> uploadImage(@RequestParam("upload") MultipartFile file) {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select a file!", HttpStatus.OK);
		}

		String staticDir = "C:/Like_Lion_BE10/personal_project/Personal_Project3_Save";
		String uploadDir = "/posts/images/";
		String filename = UUID.randomUUID() + file.getOriginalFilename();
		String savePath = staticDir + uploadDir + filename;

		File dir = new File(staticDir + uploadDir);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {
			InputStream inputStream = file.getInputStream();
			StreamUtils.copy(inputStream, new FileOutputStream(savePath));

			String url = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(uploadDir)
				.path(filename)
				.toUriString();

			Map<String, Object> response = new HashMap<>();
			response.put("uploaded", true);
			response.put("url", url);

			return ResponseEntity.ok(response);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/main-image")
	public ResponseEntity uploadMainImage(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select a file!", HttpStatus.OK);
		}

		String staticDir = "C:/Like_Lion_BE10/personal_project/Personal_Project3_Save";
		String uploadDir = "/posts/main-images/";
		String filename = UUID.randomUUID() + file.getOriginalFilename();
		String savePath = staticDir + uploadDir + filename;

		File dir = new File(staticDir + uploadDir);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {
			InputStream inputStream = file.getInputStream();
			StreamUtils.copy(inputStream, new FileOutputStream(savePath));

			String url = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(uploadDir)
				.path(filename)
				.toUriString();

			Map<String, Object> response = new HashMap<>();
			response.put("uploaded", true);
			response.put("url", url);

			return ResponseEntity.ok(response);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{username}/saves")
	public ResponseEntity<?> getSaves(@PathVariable("username") String username) {
		Blog blog = blogService.findByUsername(username);
		if (blog == null) {
			return new ResponseEntity<>("Blog not found", HttpStatus.NOT_FOUND);
		}

		List<Post> posts = postService.findSavedPostByBlogId(blog.getId());

		return new ResponseEntity<>(posts, HttpStatus.OK);
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<?> deletePost(@PathVariable("postId") Long id) {
		postService.deleteById(id);
		return new ResponseEntity<>("Post deleted", HttpStatus.OK);
	}

	@GetMapping("/{username}")
	public ResponseEntity<?> getUserPosts(@PathVariable("username") String username,
		@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size,
		@AuthenticationPrincipal UserDetails userDetails) {

		Blog blog = blogService.findByUsername(username);
		if (blog == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog not found");
		}

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("registrationDate").descending());

		List<Post> posts;
		if (userDetails != null && username.equals(userDetails.getUsername())) {
			posts = postService.findBlogPostByBlogId(pageable, blog.getId(), true);
		} else {
			posts = postService.findBlogPostByBlogId(pageable, blog.getId(), false);
		}

		if (posts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
		}

		Map<String, Object> response = new HashMap<>();
		response.put("posts", posts);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{username}/series/{seriesId}")
	public ResponseEntity<?> getPostBySeries(@PathVariable("seriesId") Long seriesId,
		@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size,
		@AuthenticationPrincipal UserDetails userDetails, @PathVariable("username") String username) {

		Series series = seriesService.findById(seriesId);
		if (series == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Series not found");
		}

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("registrationDate"));

		List<Post> posts;
		if (userDetails != null && username.equals(userDetails.getUsername())) {
			posts = postService.findBySeries(series, true, pageable);
		} else {
			posts = postService.findBySeries(series, false, pageable);
		}

		if (posts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
		}

		Map<String, Object> response = new HashMap<>();
		response.put("posts", posts);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<?> getHomePosts(
		@RequestParam(name = "orderMethod", defaultValue = "recent") String orderMethod,
		@RequestParam(name = "page", defaultValue = "1") int page,
		@RequestParam(name = "size", defaultValue = "12") int size) {

		Pageable pageable;
		if (orderMethod.equals("recent")) {
			pageable = PageRequest.of(page - 1, size, Sort.by("registrationDate").descending());
		} else {
			pageable = PageRequest.of(page - 1, size, Sort.by("view").descending());
		}

		List<Post> posts = postService.findByIsPublicAndIsPublished(pageable, true, true);

		if (posts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
		}

		Map<String, Object> response = new HashMap<>();
		response.put("posts", posts);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/view")
	public ResponseEntity<?> addViewCount(@RequestParam("postId") Long postId) {
		postService.addView(postId);
		return ResponseEntity.ok().build();
	}
}
