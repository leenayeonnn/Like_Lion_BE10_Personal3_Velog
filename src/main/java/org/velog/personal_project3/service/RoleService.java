package org.velog.personal_project3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.velog.personal_project3.domain.Role;
import org.velog.personal_project3.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
	private final RoleRepository roleRepository;

	@Transactional(readOnly = true)
	public Role findByName(String roleName) {
		return roleRepository.findByName(roleName);
	}
}
