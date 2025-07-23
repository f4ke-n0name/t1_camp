package com.f4ken0name.github.services;

import com.f4ken0name.github.models.RefreshToken;
import com.f4ken0name.github.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${jwt.refresh.expiration.ms}")
    private long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createRefreshToken(String email) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserEmail(email);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public String validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token was expired");
        }

        return refreshToken.getUserEmail();
    }

    public String renewRefreshToken(String oldToken) {
        String email = validateRefreshToken(oldToken);
        refreshTokenRepository.deleteByToken(oldToken);
        return createRefreshToken(email);
    }

    public void revokeRefreshTokenForUser(String email) {
        refreshTokenRepository.deleteByUserEmail(email);
    }
}