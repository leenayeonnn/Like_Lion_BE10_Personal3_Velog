package org.velog.personal_project3.controller;

import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.velog.personal_project3.domain.Post;
import org.velog.personal_project3.domain.Series;
import org.velog.personal_project3.domain.Tag;
import org.velog.personal_project3.domain.User;
import org.velog.personal_project3.dto.WritePostDto;
import org.velog.personal_project3.service.BlogService;
import org.velog.personal_project3.service.PostService;
import org.velog.personal_project3.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {
	private final PostService postService;
	private final BlogService blogService;
	private final UserService userService;

	@GetMapping("/post/write")
	public String writePostForm(Model model, @AuthenticationPrincipal UserDetails userDetails,
		@RequestParam(required = false) Long postId) {
		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
		}

		WritePostDto writePostDto = new WritePostDto();
		if (postId != null) {
			Post post = postService.findById(postId);
			if (post == null) {
				return "/pages/error";
			}

			mappingPostAndWritePostDto(post, writePostDto);

		} else {
			if (userDetails != null) {
				writePostDto.setBlogId(blogService.findByUsername(userDetails.getUsername()).getId());
			}
			writePostDto.setIsPublished(true);
			writePostDto.setIsPublic(true);
		}

		model.addAttribute("writePostDto", writePostDto);
		return "pages/posts/writepost";
	}

	@PostMapping("/post/write")
	public String publishPost(@Valid @ModelAttribute("writePostDto") WritePostDto writePostDto,
		BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes,
		@AuthenticationPrincipal UserDetails userDetails) {

		if (bindingResult.hasErrors()) {
			if (userDetails != null) {
				model.addAttribute("loginUser", userDetails.getUsername());
			}

			return "pages/posts/writepost";
		}

		if (!writePostDto.getIsPublished()) {
			postService.save(writePostDto, userDetails);
			return "redirect:/saves";
		}

		redirectAttributes.addFlashAttribute("writePostDto", writePostDto);
		return "redirect:/post/publish";
	}

	@GetMapping("/post/publish")
	public String publishPost(@AuthenticationPrincipal UserDetails userDetails,
		@ModelAttribute("writePostDto") WritePostDto writePostDto,
		Model model) {

		if (writePostDto.getTitle() == null || writePostDto.getTitle().isEmpty()
			|| writePostDto.getContent() == null || writePostDto.getContent().isEmpty()) {
			return "redirect:/post/write";
		}

		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
		}

		model.addAttribute("writePostDto", writePostDto);
		return "pages/posts/publishpost";
	}

	@PostMapping("/post/publish")
	public String publishPost(@Valid @ModelAttribute("writePostDto") WritePostDto writePostDto,
		BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails userDetails) {

		if (bindingResult.hasErrors()) {
			if (userDetails != null) {
				model.addAttribute("loginUser", userDetails.getUsername());
			}
			return "pages/posts/publishpost";
		}

		System.out.println(writePostDto.getSeries());
		postService.save(writePostDto, userDetails);

		return "redirect:/";
	}

	@GetMapping("/saves")
	public String savedPost(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
		}

		return "pages/posts/savedpost";
	}

	@GetMapping("/@{username}/{postId}")
	public String postDetail(@PathVariable("postId") Long postId, @PathVariable("username") String username,
		Model model, @AuthenticationPrincipal UserDetails userDetails) {

		User user = userService.findByUsername(username);
		Post post = postService.findById(postId);

		if (user == null || post == null || !user.equals(post.getBlog().getUser())) {
			return "/pages/error";
		}

		boolean isWriter = false;
		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());

			if (user.getUsername().equals(userDetails.getUsername())) {
				isWriter = true;
			}
		}

		model.addAttribute("isWriter", isWriter);
		model.addAttribute("post", post);

		return "pages/posts/postdetail";
	}

	private void mappingPostAndWritePostDto(Post post, WritePostDto writePostDto) {
		writePostDto.setPostId(post.getId());
		writePostDto.setBlogId(post.getBlog().getId());
		writePostDto.setTitle(post.getTitle());
		writePostDto.setContent(post.getContent());
		writePostDto.setIsPublished(post.getIsPublished());

		String tagList = post.getTags().stream()
			.map(Tag::getName).collect(Collectors.joining(" "));
		writePostDto.setTagList(tagList);

		writePostDto.setMainImgUrl(post.getMainImgUrl());
		writePostDto.setIsPublic(post.getIsPublic());

		Series series = post.getSeries();
		writePostDto.setSeries(series != null ? series.getName() : null);
	}
}
