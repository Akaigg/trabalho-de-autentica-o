package com.example.auth_service.application.ports;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}