package com.example.auth_service.application.auth;

import com.auth0.jwt.JWT;
import com.example.auth_service.application.ports.PasswordHasher;
import com.example.auth_service.application.ports.TokenService;
import com.example.auth_service.domain.auth.RefreshToken;
import com.example.auth_service.domain.auth.RefreshTokenRepository;
import com.example.auth_service.domain.user.User;
import com.example.auth_service.domain.user.UserRepository;
import com.example.auth_service.domain.user.vo.Email;
import com.example.auth_service.interfaces.rest.dto.auth.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PasswordLoginHandler {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    // Explicit constructor to initialize final fields
    public PasswordLoginHandler(UserRepository userRepository, 
                                 PasswordHasher passwordHasher, 
                                 TokenService tokenService, 
                                 RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public TokenResponse handle(String emailRaw, String pwRaw) {
        Email email = Email.of(emailRaw);
        Optional<User> userOptional = userRepository.findByEmail(email.getValue());

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credencial invalido");
        }

        User user = userOptional.get();
        if (!passwordHasher.match(pwRaw, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credencial invalido");
        }

        TokenService.TokenPair pair = tokenService.issue(user);

        RefreshToken refreshToken = new RefreshToken(user, pair.refreshToken(), JWT.decode(pair.refreshToken()).getExpiresAt().toInstant());
        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(pair.token(), pair.refreshToken(), pair.expiresIn());
    }
}