package org.velog.personal_project3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	Post findByBlogAndTitle(Blog blog, String title);
}
