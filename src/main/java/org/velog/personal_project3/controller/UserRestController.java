package org.velog.personal_project3.controller;

import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.velog.personal_project3.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {
	private static final Pattern USERNAME_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	private final UserService userService;

	@GetMapping("/check-username")
	public ResponseEntity<String> checkUsername(@RequestParam("username") String username) {
		if(!USERNAME_PATTERN.matcher(username).matches()) {
			return ResponseEntity.badRequest().body("영문 및 숫자 조합으로 6 ~ 12자");
		}

		if(userService.findByUsername(username) != null) {
			return ResponseEntity.badRequest().body("중복된 아이디입니다");
		}

		return ResponseEntity.ok().build();
	}

	@GetMapping("/check-email")
	public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {
		if(!EMAIL_PATTERN.matcher(email).matches()) {
			return ResponseEntity.badRequest().body("유효하지 않은 형식입니다.");
		}

		if(userService.findByEmail(email) != null) {
			return ResponseEntity.badRequest().body("중복된 이메일입니다.");
		}

		return ResponseEntity.ok().build();
	}
}
