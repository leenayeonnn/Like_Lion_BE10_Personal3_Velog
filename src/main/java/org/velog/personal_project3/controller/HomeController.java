package org.velog.personal_project3.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.velog.personal_project3.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final PostService postService;

	@GetMapping("/")
	public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {

		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
		}

		return "pages/home";
	}

	@GetMapping("/welcome")
	public String welcome(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = null;
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
			username = userDetails.getUsername();
		}

		model.addAttribute("loginUser", username);

		return "pages/welcome";
	}
}
