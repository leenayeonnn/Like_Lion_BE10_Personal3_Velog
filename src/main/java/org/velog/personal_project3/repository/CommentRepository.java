package org.velog.personal_project3.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.velog.personal_project3.domain.Comment;
import org.velog.personal_project3.domain.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	Page<Comment> findByPostAndParentId(Post post, Long parentId, Pageable pageable);

	List<Comment> findByParentIdOrderByRegistrationDate(Long parentId);
}
