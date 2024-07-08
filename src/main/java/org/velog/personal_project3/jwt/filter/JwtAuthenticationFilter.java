package org.velog.personal_project3.jwt.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.velog.personal_project3.jwt.exception.JwtExceptionCode;
import org.velog.personal_project3.jwt.token.JwtAuthenticationToken;
import org.velog.personal_project3.jwt.util.JwtTokenizer;
import org.velog.personal_project3.security.CustomUserDetails;
import org.velog.personal_project3.service.RefreshTokenService;
import org.velog.personal_project3.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenizer jwtTokenizer;
	private final RefreshTokenService refreshTokenService;
	private final UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String accessToken = getToken(request, "accessToken");
		if (StringUtils.hasText(accessToken)) {
			try {
				getAuthentication(accessToken, "accessToken", response);
			} catch (ExpiredJwtException e) {
				request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
				log.error("Expired Token : {}", accessToken, e);
				throw new BadCredentialsException("Expired token exception", e);
			} catch (UnsupportedJwtException e) {
				request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
				log.error("Unsupported Token: {}", accessToken, e);
				throw new BadCredentialsException("Unsupported token exception", e);
			} catch (MalformedJwtException e) {
				request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
				log.error("Invalid Token: {}", accessToken, e);
				throw new BadCredentialsException("Invalid token exception", e);
			} catch (IllegalArgumentException e) {
				request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
				log.error("Token not found: {}", accessToken, e);
				throw new BadCredentialsException("Token not found exception", e);
			} catch (Exception e) {
				log.error("JWT Filter - Internal Error: {}", accessToken, e);
				throw new BadCredentialsException("JWT filter internal exception", e);
			}
		} else {
			String refreshToken = getToken(request, "refreshToken");
			if (StringUtils.hasText(refreshToken)) {
				try {
					getAuthentication(refreshToken, "refreshToekn", response);
				} catch (ExpiredJwtException e) {
					request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
					log.error("Expired Token : {}", refreshToken, e);
					throw new BadCredentialsException("Expired token exception", e);
				} catch (UnsupportedJwtException e) {
					request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
					log.error("Unsupported Token: {}", refreshToken, e);
					throw new BadCredentialsException("Unsupported token exception", e);
				} catch (MalformedJwtException e) {
					request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
					log.error("Invalid Token: {}", refreshToken, e);
					throw new BadCredentialsException("Invalid token exception", e);
				} catch (IllegalArgumentException e) {
					request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
					log.error("Token not found: {}", refreshToken, e);
					throw new BadCredentialsException("Token not found exception", e);
				} catch (Exception e) {
					log.error("JWT Filter - Internal Error: {}", refreshToken, e);
					throw new BadCredentialsException("JWT filter internal exception", e);
				}
			}

		}
		filterChain.doFilter(request, response);
	}

	private void getAuthentication(String token, String tokenName, HttpServletResponse response) {
		Claims claims = jwtTokenizer.parseAccessToken(token);
		String email = claims.getSubject();
		Long userId = claims.get("userId", Long.class);
		String name = claims.get("name", String.class);
		String username = claims.get("username", String.class);
		List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

		if ("refreshToken".equals(tokenName)) {
			List<String> roles = (List<String>)claims.get("roles");

			String accessToken = jwtTokenizer.createAccessToken(userId, email, name, username, roles);

			Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
			accessTokenCookie.setHttpOnly(true);  //보안 (쿠키값을 자바스크립트같은곳에서는 접근할수 없어요.)
			accessTokenCookie.setPath("/");
			accessTokenCookie.setMaxAge(
				Math.toIntExact(
					JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000)); //30분 쿠키의 유지시간 단위는 초 ,  JWT의 시간단위는 밀리세컨드

			response.addCookie(accessTokenCookie);
		}

		CustomUserDetails userDetails = new CustomUserDetails(username, "", name,
			authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

		Authentication authentication = new JwtAuthenticationToken(authorities, userDetails, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
		List<String> roles = (List<String>)claims.get("roles");
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (String role : roles) {
			authorities.add(() -> role);
		}
		return authorities;
	}

	private String getToken(HttpServletRequest request, String tokenName) {
		String authorization = request.getHeader("Authorization");
		if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
			return authorization.substring(7);
		}

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (tokenName.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}
}
