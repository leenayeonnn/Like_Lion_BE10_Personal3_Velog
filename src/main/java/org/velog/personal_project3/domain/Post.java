package org.velog.personal_project3.domain;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "posts")
@Getter
@Setter
@ToString
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "content", nullable = false, length = 10000)
	private String content;

	@Column(name = "registration_date", nullable = false)
	private LocalDateTime registrationDate;

	@Column(name = "is_public", nullable = false)
	private Boolean isPublic;

	@Column(name = "is_published", nullable = false)
	private Boolean isPublished;

	@Column(name = "main_img_url")
	private String mainImgUrl;

	@ManyToOne
	@JoinColumn(name = "blog_id", nullable = false)
	private Blog blog;

	@ManyToMany
	@JoinTable(name = "post_tags",
		joinColumns = @JoinColumn(name = "post_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id")
	)
	private Set<Tag> tags;

	@ManyToOne
	@JoinTable(name = "post_series",
		joinColumns = @JoinColumn(name = "post_id"),
		inverseJoinColumns = @JoinColumn(name = "series_id")
	)
	private Series series;
}
