package org.velog.personal_project3.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.velog.personal_project3.domain.User;
import org.velog.personal_project3.service.RefreshTokenService;
import org.velog.personal_project3.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {
	private static final Pattern USERNAME_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{8,16}$");

	private final UserService userService;
	private final RefreshTokenService refreshTokenService;
	private final PasswordEncoder passwordEncoder;

	@GetMapping("/check-username")
	public ResponseEntity<String> checkUsername(@RequestParam("username") String username) {
		if (!USERNAME_PATTERN.matcher(username).matches()) {
			return ResponseEntity.badRequest().body("영문 및 숫자 조합으로 6 ~ 12자");
		}

		if (userService.findByUsername(username) != null) {
			return ResponseEntity.badRequest().body("중복된 아이디입니다");
		}

		return ResponseEntity.ok().build();
	}

	@GetMapping("/check-email")
	public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			return ResponseEntity.badRequest().body("유효하지 않은 형식입니다.");
		}

		if (userService.findByEmail(email) != null) {
			return ResponseEntity.badRequest().body("중복된 이메일입니다.");
		}

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/withdrawal")
	public ResponseEntity<String> withdrawal(HttpServletResponse response, @RequestParam("password") String password,
		@AuthenticationPrincipal UserDetails userDetails) {

		User user = userService.findByUsername(userDetails.getUsername());
		if (!passwordEncoder.matches(password, user.getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
		}

		refreshTokenService.deleteRefreshToken(userDetails.getUsername());

		Cookie accessTokenCookie = new Cookie("accessToken", null);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setMaxAge(0);

		Cookie refreshTokenCookie = new Cookie("refreshToken", null);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(0);

		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);

		userService.delete(user);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/password")
	public ResponseEntity<String> setPassword(@RequestParam("oldPassword") String oldPassword,
		@RequestParam("newPassword") String newPassword, @RequestParam("newPasswordCheck") String newPasswordCheck,
		@AuthenticationPrincipal UserDetails userDetails) {

		User user = userService.findByUsername(userDetails.getUsername());
		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			return ResponseEntity.badRequest().body("oldPassword");
		}

		if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
			return ResponseEntity.badRequest().body("pattern");
		}

		if (!newPassword.equals(newPasswordCheck)) {
			return ResponseEntity.badRequest().body("newPassword");
		}

		userService.setPassword(user, newPassword);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/upload-profile")
	public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("noFile");
		}

		String staticDir = "C:/Like_Lion_BE10/personal_project/Personal_Project3_Save";
		String uploadDir = "/users/profile-images/";
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
			return ResponseEntity.badRequest().body("failUpload");
		}
	}

	@PutMapping("/profile")
	public ResponseEntity<String> setProfileUrl(@RequestParam("imgUrl") String imgUrl,
		@AuthenticationPrincipal UserDetails userDetails) {

		User user = userService.findByUsername(userDetails.getUsername());
		userService.setProfileUrl(user, imgUrl);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/name")
	public ResponseEntity<String> setName(@RequestParam("newName") String newName,
		@AuthenticationPrincipal UserDetails userDetails) {

		User user = userService.findByUsername(userDetails.getUsername());
		userService.setName(user, newName);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/email")
	public ResponseEntity<String> setEmail(@RequestParam("newEmail") String newEmail,
		@AuthenticationPrincipal UserDetails userDetails) {

		if(!EMAIL_PATTERN.matcher(newEmail).matches()){
			return ResponseEntity.badRequest().body("pattern");
		}

		User user = userService.findByUsername(userDetails.getUsername());
		User findByEmail = userService.findByEmail(user.getEmail());
		if(findByEmail != null && !user.equals(findByEmail)){
			return ResponseEntity.badRequest().body("duplicate");
		}

		userService.setEmail(user, newEmail);

		return ResponseEntity.ok().build();
	}
}
