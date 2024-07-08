package org.velog.personal_project3.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.velog.personal_project3.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByValue(String refreshToken);

	Optional<RefreshToken> findByUserId(Long userId);

	void deleteByUserId(Long userId);
}
