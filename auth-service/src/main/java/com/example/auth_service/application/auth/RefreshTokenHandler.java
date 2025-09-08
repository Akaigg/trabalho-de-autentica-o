package com.example.auth_service.application.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.auth_service.application.ports.TokenService;
import com.example.auth_service.domain.auth.RefreshToken;
import com.example.auth_service.domain.auth.RefreshTokenRepository;
import com.example.auth_service.domain.user.User;
import com.example.auth_service.domain.user.UserRepository;
import com.example.auth_service.interfaces.rest.dto.auth.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenHandler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public TokenResponse handle(String refreshToken) {
        DecodedJWT decodedJWT = JWT.decode(refreshToken);
        String userId = decodedJWT.getSubject();

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido"));

        if (storedToken.isExpired() || storedToken.isRevoked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirado ou revogado");
        }

        storedToken.revoke();
        refreshTokenRepository.save(storedToken);

        TokenService.TokenPair newTokens = tokenService.issue(user);

        RefreshToken newRefreshToken = new RefreshToken(user, newTokens.refreshToken(),
                JWT.decode(newTokens.refreshToken()).getExpiresAt().toInstant());
        refreshTokenRepository.save(newRefreshToken);

        return new TokenResponse(newTokens.token(), newTokens.refreshToken(), newTokens.expiresIn());
    }

    public void logout(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token inválido"));

        storedToken.revoke();
        refreshTokenRepository.save(storedToken);
    }
}