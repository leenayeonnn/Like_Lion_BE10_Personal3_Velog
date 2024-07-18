package org.velog.personal_project3.service;

import org.springframework.stereotype.Service;
import org.velog.personal_project3.domain.Tag;
import org.velog.personal_project3.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
	private final TagRepository tagRepository;

	public Tag findByName(String tagName) {
		return tagRepository.findByName(tagName);
	}

	public void save(Tag tag) {
		tagRepository.save(tag);
	}
}
