package org.velog.personal_project3.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.velog.personal_project3.domain.Series;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
	List<Series> findByBlogId(Long blogId);

	List<Series> findByBlogId(Long blogId, Pageable pageable);

	Series findByBlogIdAndName(Long blogId, String name);
}
