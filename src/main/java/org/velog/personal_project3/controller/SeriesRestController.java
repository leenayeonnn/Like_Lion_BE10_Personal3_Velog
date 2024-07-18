package org.velog.personal_project3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.Series;
import org.velog.personal_project3.service.BlogService;
import org.velog.personal_project3.service.PostService;
import org.velog.personal_project3.service.SeriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesRestController {
	private final SeriesService seriesService;
	private final BlogService blogService;

	@GetMapping
	public ResponseEntity<List<String>> getSeriesNames(@RequestParam("blogId") Long blogId) {
		List<String> seriesNames = seriesService.findByBlogId(blogId).stream()
			.map(Series::getName)
			.collect(Collectors.toList());
		return ResponseEntity.ok(seriesNames);
	}

	@PostMapping
	public ResponseEntity<?> createSeries(@RequestParam("seriesName") String seriesName,
		@RequestParam("blogId") Long blogId) {

		if(seriesService.findByBlogIdAndName(blogId, seriesName) != null){
			return ResponseEntity.badRequest().body("Series already exists");
		}

		Blog blog = blogService.findById(blogId);
		Series series = new Series();
		series.setName(seriesName);
		series.setBlog(blog);
		seriesService.save(series);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/{username}")
	public ResponseEntity<?> getSeries(@PathVariable("username") String username,
		@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size) {

		Blog blog = blogService.findByUsername(username);
		if (blog == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog not found");
		}

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		List<Series> series = seriesService.findBlogSeriesByBlogId(blog.getId(), pageable);

		if (series.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Series not found");
		}

		Map<String, Object> response = new HashMap<>();
		response.put("series", series);

		return ResponseEntity.ok(response);
	}
}
