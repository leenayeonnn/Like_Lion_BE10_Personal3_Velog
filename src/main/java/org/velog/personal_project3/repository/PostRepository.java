package org.velog.personal_project3.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.Post;
import org.velog.personal_project3.domain.Series;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	Page<Post> findByIsPublicAndIsPublished(Pageable pageable, boolean isPublic, boolean isPublished);

	List<Post> findByBlogIdAndIsPublishedOrderByRegistrationDateDesc(Long blogId, boolean isPublished);

	List<Post> findByBlogIdAndIsPublishedOrderByRegistrationDateDesc(Long blogId, boolean isPublished, Pageable pageable);

	List<Post> findByBlogIdAndIsPublishedAndIsPublicOrderByRegistrationDateDesc(Long blogId, boolean isPublished, boolean isPublic, Pageable pageable);

	List<Post> findBySeriesAndIsPublished(Series series, boolean isPublished, Pageable pageable);

	List<Post> findBySeriesAndIsPublishedAndIsPublic(Series series, boolean isPublished, boolean isPublic, Pageable pageable);
}
