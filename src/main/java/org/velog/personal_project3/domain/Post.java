package org.velog.personal_project3.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", nullable = false)
	@NotEmpty(message="필수 항목입니다.")
	private String title;

	@Column(name = "content", nullable = false)
	@NotEmpty(message="필수 항목입니다.")
	private String content;

	@Column(name = "registration_date", nullable = false)
	private LocalDateTime registrationDate;

	@Column(name = "is_public", nullable = false)
	@NotNull(message="필수 항목입니다.")
	private Boolean isPublic;

	@Column(name = "is_published", nullable = false)
	@NotNull(message="필수 항목입니다.")
	private Boolean isPublished;

	@Column(name = "main_img_url")
	private String mainImgUrl;

	@ManyToOne
	@JoinColumn(name = "blog_id", nullable = false)
	private Blog blog;
}
