package org.velog.personal_project3.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentDto {
	private Long id;
	private String writerUsername;
	private String writerName;
	private String content;
	private LocalDateTime registrationDate;
	private boolean writer;
}
