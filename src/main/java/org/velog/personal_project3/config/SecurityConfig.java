package org.velog.personal_project3.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.velog.personal_project3.jwt.exception.CustomAuthenticationEntryPoint;
import org.velog.personal_project3.jwt.filter.JwtAuthenticationFilter;
import org.velog.personal_project3.jwt.util.JwtTokenizer;
import org.velog.personal_project3.security.CustomUserDetailsService;
import org.velog.personal_project3.service.RefreshTokenService;
import org.velog.personal_project3.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomUserDetailsService customUserDetailsService;
	private final JwtTokenizer jwtTokenizer;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final RefreshTokenService refreshTokenService;
	private final UserService userService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/css/**", "/js/**").permitAll()
				.requestMatchers("/", "/welcome").permitAll()
				.requestMatchers("/userreg", "/api/users/check-username", "/api/users/check-email").permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/writepost", "/@{username}/{title}").permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer, refreshTokenService, userService),
				UsernamePasswordAuthenticationFilter.class)
			//                .formLogin(Customizer.withDefaults())
			.formLogin(form -> form.disable())
			.logout(logout -> logout.disable())
			.sessionManagement(sessionManagement -> sessionManagement
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(csrf -> csrf.disable())
			.httpBasic(httpBasic -> httpBasic.disable())
			.cors(cors -> cors.configurationSource(configurationSource()))
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(customAuthenticationEntryPoint));

		return http.build();
	}

	public CorsConfigurationSource configurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setAllowedMethods(List.of("GET", "POST", "DELETE"));
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}