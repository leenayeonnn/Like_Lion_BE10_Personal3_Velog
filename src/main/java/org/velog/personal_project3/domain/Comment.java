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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "content", nullable = false, length = 200)
	private String content;

	@Column(name = "registration_date", nullable = false)
	private LocalDateTime registrationDate;

	@Column(name = "parent_id")
	private Long parentId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;
}
