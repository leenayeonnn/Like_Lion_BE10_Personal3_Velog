package org.velog.personal_project3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.User;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
	Blog findByUser(User user);
}

