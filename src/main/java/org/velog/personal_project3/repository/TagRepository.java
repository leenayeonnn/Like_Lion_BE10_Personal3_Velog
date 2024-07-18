package org.velog.personal_project3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.velog.personal_project3.domain.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	Tag findByName(String tagName);
}
