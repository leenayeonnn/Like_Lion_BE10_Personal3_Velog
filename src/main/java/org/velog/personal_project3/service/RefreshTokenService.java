package org.velog.personal_project3.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.velog.personal_project3.domain.RefreshToken;
import org.velog.personal_project3.domain.User;
import org.velog.personal_project3.repository.RefreshTokenRepository;
import org.velog.personal_project3.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	@Transactional(readOnly = false)
	public RefreshToken addRefreshToken(RefreshToken refreshToken) {
		return refreshTokenRepository.save(refreshToken);
	}

	@Transactional(readOnly = true)
	public Optional<RefreshToken> findRefreshTokenByUserId(Long userId) {
		return refreshTokenRepository.findByUserId(userId);
	}

	@Transactional(readOnly = false)
	public void deleteRefreshToken(String username) {
		User user = userRepository.findByUsername(username);
		refreshTokenRepository.deleteByUserId(user.getId());
	}
}
