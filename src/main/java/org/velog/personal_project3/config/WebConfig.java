package org.velog.personal_project3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 정적 자원의 경로 설정
		registry.addResourceHandler("/posts/images/**")
			.addResourceLocations("file:C:/Like_Lion_BE10/personal_project/Personal_Project3_Save/posts/images/");

		registry.addResourceHandler("/posts/main-images/**")
			.addResourceLocations("file:C:/Like_Lion_BE10/personal_project/Personal_Project3_Save/posts/main-images/");

		registry.addResourceHandler("/users/profile-images/**")
			.addResourceLocations("file:C:/Like_Lion_BE10/personal_project/Personal_Project3_Save/users/profile-images/");
	}
}
