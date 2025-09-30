package com.example.auth_service.infrastructure.email;

import com.example.auth_service.application.ports.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class SmtpEmailService implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Redefinição de Senha");
        message.setText("Para redefinir sua senha, use o token: " + token);
        mailSender.send(message);
    }
}