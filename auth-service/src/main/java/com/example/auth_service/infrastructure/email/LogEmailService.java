package com.example.auth_service.infrastructure.email;

import com.example.auth_service.application.ports.EmailService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!prod")
public class LogEmailService implements EmailService {

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        System.out.println("Enviando e-mail de redefinição de senha para: " + to);
        System.out.println("Token: " + token);
    }
}