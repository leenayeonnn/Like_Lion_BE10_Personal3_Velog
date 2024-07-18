package org.velog.personal_project3.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.velog.personal_project3.domain.Blog;
import org.velog.personal_project3.domain.User;
import org.velog.personal_project3.repository.BlogRepository;
import org.velog.personal_project3.repository.RoleRepository;
import org.velog.personal_project3.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final BlogRepository blogRepository;

	@Transactional(readOnly = false)
	public void regUser(User user) {
		user.setRegistrationDate(LocalDateTime.now());
		user.getRoles().add(roleRepository.findByName("USER"));

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Transactional(readOnly = true)
	public User findById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public void delete(User user) {
		userRepository.delete(user);
	}

	public void setPassword(User user, String newPassword) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	public void setProfileUrl(User user, String imgUrl) {
		user.setProfileUrl(imgUrl);
		userRepository.save(user);
	}

	public void setName(User user, String newName) {
		Blog blog = blogRepository.findByUser(user);
		blog.setName(newName);
		blogRepository.save(blog);

		user.setName(newName);
		userRepository.save(user);
	}

	public void setEmail(User user, String newEmail) {
		user.setEmail(newEmail);
		userRepository.save(user);
	}
}
