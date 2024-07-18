package org.velog.personal_project3.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.velog.personal_project3.domain.RefreshToken;
import org.velog.personal_project3.domain.Role;
import org.velog.personal_project3.domain.User;
import org.velog.personal_project3.dto.LoginDto;
import org.velog.personal_project3.jwt.util.JwtTokenizer;
import org.velog.personal_project3.service.BlogService;
import org.velog.personal_project3.service.RefreshTokenService;
import org.velog.personal_project3.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService userService;
	private final BlogService blogService;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenizer jwtTokenizer;
	private final RefreshTokenService refreshTokenService;

	@GetMapping("/userreg")
	public String userregform(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
		}

		model.addAttribute("user", new User());
		return "pages/users/userregform";
	}

	@PostMapping("/userreg")
	public String userreg(@ModelAttribute("user") User user) {

		userService.regUser(user);
		blogService.makeBlog(user);

		return "redirect:/welcome";
	}

	@GetMapping("/login")
	public String loginform(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
		}

		model.addAttribute("loginDto", new LoginDto());

		return "pages/users/loginform";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute("loginDto") LoginDto loginDto, Model model, HttpServletResponse response) {
		User user = userService.findByUsername(loginDto.getUsername());
		if (user == null || !passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
			model.addAttribute("failLogin", "유효하지 않은 아이디, 비밀번호 입니다.");
			return "pages/users/loginform";
		}

		List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

		String accessToken = jwtTokenizer.createAccessToken(
			user.getId(), user.getEmail(), user.getName(), user.getUsername(), roles);
		String refreshToken = jwtTokenizer.createRefreshToken(
			user.getId(), user.getEmail(), user.getName(), user.getUsername(), roles);

		RefreshToken refreshTokenEntity = new RefreshToken();
		refreshTokenEntity.setValue(refreshToken);
		refreshTokenEntity.setUserId(user.getId());

		refreshTokenService.deleteRefreshToken(user.getUsername());
		refreshTokenService.addRefreshToken(refreshTokenEntity);

		Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
		accessTokenCookie.setHttpOnly(true);  //보안 (쿠키값을 자바스크립트같은곳에서는 접근할수 없어요.)
		accessTokenCookie.setPath("/");
		accessTokenCookie.setMaxAge(
			Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000)); //30분 쿠키의 유지시간 단위는 초 ,  JWT의 시간단위는 밀리세컨드

		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000)); //7일

		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);

		return "redirect:/";
	}

	@GetMapping("/logout")
	public String logout(HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			refreshTokenService.deleteRefreshToken(userDetails.getUsername());
		}

		Cookie accessTokenCookie = new Cookie("accessToken", null);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setMaxAge(0);

		Cookie refreshTokenCookie = new Cookie("refreshToken", null);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(0);

		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);

		return "redirect:/";
	}

	@GetMapping("/setting")
	public String setting(Model model, @AuthenticationPrincipal UserDetails userDetails) {

		if (userDetails != null) {
			model.addAttribute("loginUser", userDetails.getUsername());
			model.addAttribute("user", userService.findByUsername(userDetails.getUsername()));
		}

		return "pages/users/setting";
	}
}
