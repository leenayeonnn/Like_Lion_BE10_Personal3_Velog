package org.velog.personal_project3.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.Post;
import org.velog.personal_project3.service.BlogService;
import org.velog.personal_project3.service.PostService;
import org.velog.personal_project3.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
	private final BlogService blogService;
	private final UserService userService;

	@GetMapping("/writepost")
	public String writePostForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
		}

		model.addAttribute("post", new Post());
		return "pages/posts/writepost";
	}

	@PostMapping("/writepost")
	public String writePost(@Valid @ModelAttribute("post") Post post, BindingResult bindingResult,
		Model model, @AuthenticationPrincipal UserDetails userDetails) {
		if (bindingResult.hasErrors()) {
			if (userDetails != null) {
				model.addAttribute("loginUser", userDetails.getUsername());
			}

			return "pages/posts/writepost";
		}

		postService.savePost(post, userDetails);

		return "redirect:/";
	}

	@GetMapping("/@{username}/{title}")
	public String postDetail(@PathVariable String username, @PathVariable String title, Model model,
		@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
		}

		Blog blog = blogService.findByUser(userService.findByUsername(username));
		Post post = postService.findByBlogAndTitle(blog, title);
		model.addAttribute("post", post);
		return "pages/posts/postdetail";
	}
}
