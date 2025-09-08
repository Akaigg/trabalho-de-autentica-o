package com.example.auth_service;

import com.example.auth_service.domain.auth.RefreshTokenRepository;
import com.example.auth_service.interfaces.rest.dto.auth.PasswordLoginRequest;
import com.example.auth_service.interfaces.rest.dto.auth.RefreshTokenRequest;
import com.example.auth_service.interfaces.rest.dto.auth.TokenResponse;
import com.example.auth_service.interfaces.rest.dto.user.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Garante que os testes sejam transacionais e revertidos
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        // Limpar o repositório antes de cada teste
        refreshTokenRepository.deleteAll();
    }

    @Test
    void shouldPerformFullAuthFlowSuccessfully() throws Exception {
        // 1. Registrar um novo usuário
        UserRequest userRequest = new UserRequest("Test User", "test@example.com", "password123");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated());

        // 2. Fazer login
        PasswordLoginRequest loginRequest = new PasswordLoginRequest("test@example.com", "password123");
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        TokenResponse loginTokenResponse = objectMapper.readValue(loginResponse, TokenResponse.class);
        String originalRefreshToken = loginTokenResponse.refreshToken();

        // Verifica se o token de refresh original foi salvo
        assertTrue(refreshTokenRepository.findByTokenHash(originalRefreshToken).isPresent());

        // 3. Usar o refresh token para obter novos tokens
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(originalRefreshToken);
        MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();
        
        String refreshResponse = refreshResult.getResponse().getContentAsString();
        TokenResponse refreshTokens = objectMapper.readValue(refreshResponse, TokenResponse.class);

        // highlight-start
        // VERIFICAÇÃO CORRIGIDA: O token original DEVE estar revogado agora.
        assertTrue(refreshTokenRepository.findByTokenHash(originalRefreshToken).get().isRevoked());
        // highlight-end

        // Verifica se o NOVO token de refresh foi salvo
        assertTrue(refreshTokenRepository.findByTokenHash(refreshTokens.refreshToken()).isPresent());

        // 4. Fazer logout com o novo token de refresh
        RefreshTokenRequest logoutRequest = new RefreshTokenRequest(refreshTokens.refreshToken());
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isNoContent());
        
        // Verifica se o token de refresh (usado no logout) foi revogado
        assertTrue(refreshTokenRepository.findByTokenHash(refreshTokens.refreshToken()).get().isRevoked());

        // 5. Tentar usar o token revogado novamente (deve falhar)
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isUnauthorized());
    }
}