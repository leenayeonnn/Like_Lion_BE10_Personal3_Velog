package org.velog.personal_project3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.velog.personal_project3.domain.Comment;
import org.velog.personal_project3.domain.User;
import org.velog.personal_project3.dto.CommentDto;
import org.velog.personal_project3.service.CommentService;
import org.velog.personal_project3.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {
	private final CommentService commentService;
	private final UserService userService;

	@GetMapping
	private ResponseEntity<?> getCommentsByPost(@RequestParam("postId") Long postId,
		@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size,
		@AuthenticationPrincipal UserDetails userDetails) {

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("registrationDate").descending());

		Page<Comment> comments = commentService.findByPostId(postId, pageable);

		if (comments.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("comment no found");
		}

		List<CommentDto> commentDtos = new ArrayList<>();
		for (Comment comment : comments) {
			User user = userService.findById(comment.getUserId());
			CommentDto commentDto = new CommentDto(comment.getId(), user.getUsername(), user.getName(),
				comment.getContent(), comment.getRegistrationDate(), false);

			if (userDetails != null && user.getUsername().equals(userDetails.getUsername())) {
				commentDto.setWriter(true);
			}

			commentDtos.add(commentDto);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("comments", commentDtos);
		response.put("currentPage", comments.getNumber() + 1);
		response.put("totalPages", comments.getTotalPages());
		return ResponseEntity.ok(response);
	}

	@PostMapping
	private ResponseEntity<?> saveComment(@RequestParam("postId") Long postId,
		@RequestParam("content") String content, @RequestParam(name = "parentId", defaultValue = "0") Long parentId,
		@AuthenticationPrincipal UserDetails userDetails) {

		if (userDetails != null) {
			commentService.saveComment(postId, content, userDetails.getUsername(), parentId);
		}

		return ResponseEntity.ok("save comment");
	}

	@PutMapping
	private ResponseEntity<?> editComment(@RequestParam("commentId") Long commentId,
		@RequestParam("content") String content, @AuthenticationPrincipal UserDetails userDetails) {

		Comment comment = commentService.findById(commentId);

		if (comment == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("comment no found");
		}

		User writer = userService.findById(comment.getUserId());

		if (!writer.getUsername().equals(userDetails.getUsername())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("writer != editor");
		}

		comment.setContent(content);
		commentService.editComment(comment);
		return ResponseEntity.ok("edit comment");
	}

	@DeleteMapping
	private ResponseEntity<?> deleteComment(@RequestParam("commentId") Long commentId,
		@AuthenticationPrincipal UserDetails userDetails) {

		Comment comment = commentService.findById(commentId);

		if (comment == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("comment no found");
		}

		User writer = userService.findById(comment.getUserId());

		if (!writer.getUsername().equals(userDetails.getUsername())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("writer != deleter");
		}

		commentService.delete(comment);
		return ResponseEntity.ok("delete comment");
	}

	@GetMapping("/sub")
	private ResponseEntity<?> getSubComments(@RequestParam("parentId") Long parentId,
		@AuthenticationPrincipal UserDetails userDetails) {

		List<Comment> comments = commentService.findSubByParentId(parentId);

		if (comments.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("comment no found");
		}

		List<CommentDto> commentDtos = new ArrayList<>();
		for (Comment comment : comments) {
			User user = userService.findById(comment.getUserId());
			CommentDto commentDto = new CommentDto(comment.getId(), user.getUsername(), user.getName(),
				comment.getContent(), comment.getRegistrationDate(), false);

			if (userDetails != null && user.getUsername().equals(userDetails.getUsername())) {
				commentDto.setWriter(true);
			}

			commentDtos.add(commentDto);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("comments", commentDtos);
		return ResponseEntity.ok(response);
	}

}
