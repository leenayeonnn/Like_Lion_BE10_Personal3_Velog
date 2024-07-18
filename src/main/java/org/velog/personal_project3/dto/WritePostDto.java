package org.velog.personal_project3.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WritePostDto {
	private Long postId;

	private Long blogId;

	@NotEmpty(message = "필수 요소 입니다.")
	private String title;

	@NotEmpty(message = "필수 요소 입니다.")
	private String content;

	@NotNull(message = "필수 요소 입니다.")
	private Boolean isPublished;

	private String tagList;

	private String mainImgUrl;

	@NotNull(message = "필수 요소 입니다.")
	private Boolean isPublic;

	private String series;
}
