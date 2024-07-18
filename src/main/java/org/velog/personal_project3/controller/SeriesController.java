package org.velog.personal_project3.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.Series;
import org.velog.personal_project3.service.BlogService;
import org.velog.personal_project3.service.SeriesService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/@{username}/series")
@RequiredArgsConstructor
public class SeriesController {
	private final SeriesService seriesService;
	private final BlogService blogService;

	@GetMapping("/{seriesId}")
	public String seriesDetail(@PathVariable("seriesId") Long seriesId, @PathVariable("username") String username,
		Model model, @AuthenticationPrincipal UserDetails userDetails) {

		Series series = seriesService.findById(seriesId);
		Blog blog = blogService.findByUsername(username);

		if (blog != null && blog.equals(series.getBlog())) {
			model.addAttribute("series", series);
		} else {
			return "pages/error";
		}

		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
		}

		model.addAttribute("blogUsername", username);

		return "pages/series/seriesdetail";
	}
}