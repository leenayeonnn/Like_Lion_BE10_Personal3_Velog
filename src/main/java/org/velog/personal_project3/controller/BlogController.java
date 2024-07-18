package org.velog.personal_project3.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.velog.personal_project3.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/@{username}")
@RequiredArgsConstructor
public class BlogController {
	private final UserService userService;

	@GetMapping
	public String posts(@PathVariable("username") String username, Model model,
		@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
		}

		if(userService.findByUsername(username) == null) {
			return "pages/error";
		}

		model.addAttribute("blogUsername", username);
		return "pages/blogs/blog";
	}
}
