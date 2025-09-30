package com.example.auth_service.interfaces.rest;

import com.example.auth_service.application.auth.PasswordLoginHandler;
import com.example.auth_service.application.auth.PasswordResetService;
import com.example.auth_service.application.auth.RefreshTokenHandler;
import com.example.auth_service.interfaces.rest.dto.auth.PasswordLoginRequest;
import com.example.auth_service.interfaces.rest.dto.auth.RefreshTokenRequest;
import com.example.auth_service.interfaces.rest.dto.auth.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final PasswordLoginHandler passwordLoginHandler;
    private final RefreshTokenHandler refreshTokenHandler;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginWithPassword(@Valid @RequestBody PasswordLoginRequest request) {
        TokenResponse token = passwordLoginHandler.handle(request.email(), request.password());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse token = refreshTokenHandler.handle(request.refreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenHandler.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody Map<String, String> payload) {
        passwordResetService.requestPasswordReset(payload.get("email"));
        return ResponseEntity.ok().build();
    }
}