package com.example.auth_service.application.auth;

import com.example.auth_service.application.ports.EmailService;
import com.example.auth_service.domain.user.User;
import com.example.auth_service.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = UUID.randomUUID().toString();
        // Salve o token de redefinição de senha no usuário ou em uma tabela separada

        emailService.sendPasswordResetEmail(user.getEmail().getValue(), token);
    }
}