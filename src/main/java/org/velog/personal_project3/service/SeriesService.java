package org.velog.personal_project3.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.velog.personal_project3.domain.Series;
import org.velog.personal_project3.repository.SeriesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeriesService {
	private final SeriesRepository seriesRepository;

	public List<Series> findByBlogId(Long blogId) {
		return seriesRepository.findByBlogId(blogId);
	}

	public List<Series> findBlogSeriesByBlogId(Long blogId, Pageable pageable) {
		return seriesRepository.findByBlogId(blogId, pageable);
	}

	public void save(Series series) {
		seriesRepository.save(series);
	}

	public Series findByBlogIdAndName(Long blogId, String name) {
		return seriesRepository.findByBlogIdAndName(blogId, name);
	}

	public Series findById(Long id) {
		return seriesRepository.findById(id).orElse(null);
	}
}
