package com.example.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.entity.RefreshToken;
import com.example.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    public RefreshToken generateRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken(null, UUID.randomUUID().toString(), username,
                Instant.now().plusMillis(refreshExpirationMs));
        return refreshTokenRepository.save(refreshToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .filter(token -> token.getExpiryDate().isAfter(Instant.now())).isPresent();
    }

    public String getUsername(String refreshToken) {
        Optional<RefreshToken> result = refreshTokenRepository.findByToken(refreshToken);
        if (result.isPresent()) {
            return result.get().getUsername();
        }
        return null;
    }

    public void deleteByUsername(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }

    @Scheduled(fixedRate = 86400000)
    @Transactional
    public void deleteExpiredRefreshTokens() {
        Instant now = Instant.now();
        int deletedCount = refreshTokenRepository.deleteExpiredRefreshTokens(now);
        System.out.println("Deleted " + deletedCount + " expired refresh tokens.");
    }
}
