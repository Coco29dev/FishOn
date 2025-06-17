package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Service.AuthService;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.DTO.Auth.*;
import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.Config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class) // ✅ AJOUT : Import de votre configuration de sécurité
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private UserModel testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO(
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                25,
                "password123"
        );

        loginRequest = new LoginRequestDTO("test@example.com", "password123");

        testUser = new UserModel(
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                25,
                "encodedPassword",
                "profile.jpg"
        );
        testUser.setId(UUID.randomUUID());
    }

    @Test
    void register_Success() throws Exception {
        // Given
        when(authService.register(any(UserModel.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(authService).register(any(UserModel.class));
    }

    @Test
    void register_ValidationError_MissingEmail() throws Exception {
        // Given
        registerRequest.setEmail(""); // Email vide

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_ValidationError_InvalidEmail() throws Exception {
        // Given
        registerRequest.setEmail("invalid-email"); // Format invalide

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ValidationError_AgeTooYoung() throws Exception {
        // Given
        registerRequest.setAge(5); // Trop jeune

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest()); // ✅ CORRIGÉ : andExpect au lieu de andExpected
    }

    @Test
    void login_Success() throws Exception {
        // Given
        Authentication mockAuth = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(userDetails);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_ValidationError_MissingPassword() throws Exception {
        // Given
        loginRequest.setPassword(""); // Mot de passe vide

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_ValidationError_PasswordTooShort() throws Exception {
        // Given
        loginRequest.setPassword("123"); // Trop court

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void logout_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Déconnexion réussie"));
    }
}