package org.velog.personal_project3.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PublishPostDto {
	private Long postId;
	private Long blogId;
	private String mainImgUrl;
	@NotNull(message = "필수 요소 입니다.")
	private Boolean isPublic;
	private String series;
}
