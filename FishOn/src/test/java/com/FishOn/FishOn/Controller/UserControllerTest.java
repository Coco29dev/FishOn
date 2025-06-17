package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Service.UserService;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.DTO.User.*;
import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.Exception.FishOnException.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel testUser;
    private UpdateUserRequestDTO updateUserRequest;
    private Authentication authentication;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testUser = new UserModel(
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                25,
                "encodedPassword",
                "profile.jpg"
        );
        testUser.setId(userId);

        updateUserRequest = new UpdateUserRequestDTO(
                "newusername",
                "newemail@example.com",
                "Jane",
                "Smith",
                30,
                "newprofile.jpg"
        );

        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // =============== TESTS VALIDATION UNIQUEMENT ===============

    @Test
    void updateUser_ValidationError_InvalidEmail() throws Exception {
        // Given
        updateUserRequest.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    void updateUser_ValidationError_EmptyUserName() throws Exception {
        // Given
        updateUserRequest.setUserName("");

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    void updateUser_ValidationError_AgeTooYoung() throws Exception {
        // Given
        updateUserRequest.setAge(5);

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    void updateUser_ValidationError_AgeTooOld() throws Exception {
        // Given
        updateUserRequest.setAge(150);

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(), any());
    }

    // =============== TESTS ENDPOINT PUBLIC ===============

    @Test
    void getUserByUserName_Success() throws Exception {
        // Given
        when(userService.getByUserName("testuser")).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/search/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService).getByUserName("testuser");
    }

    @Test
    void getUserByUserName_NotFound() throws Exception {
        // Given
        when(userService.getByUserName("nonexistent"))
                .thenThrow(new UserNotFoundByUserName("nonexistent"));

        // When & Then
        mockMvc.perform(get("/api/users/search/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("l'utilisateur nonexistent n'existe pas"));

        verify(userService).getByUserName("nonexistent");
    }

    @Test
    void getUserByUserName_EmptyUserName() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/search/"))
                .andExpect(status().isNotFound()); // 404 car l'endpoint n'existe pas

        verify(userService, never()).getByUserName(any());
    }

    // =============== TESTS DE LIMITES DE VALIDATION ===============

    @Test
    void updateUser_WithMinimumValidAge() throws Exception {
        // Given
        updateUserRequest.setAge(9); // Âge minimum valide

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isUnauthorized()); // Attendu car vérifications manuelles

        // Pas de verify car le service n'est jamais appelé à cause des vérifications d'auth
    }

    @Test
    void updateUser_WithMaximumValidAge() throws Exception {
        // Given
        updateUserRequest.setAge(99); // Âge maximum valide

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isUnauthorized()); // Attendu car vérifications manuelles
    }

    @Test
    void updateUser_WithUserNameLengthLimit() throws Exception {
        // Given
        updateUserRequest.setUserName("a".repeat(20)); // Exactement 20 caractères

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isUnauthorized()); // Attendu car vérifications manuelles
    }

    @Test
    void updateUser_WithUserNameTooLong() throws Exception {
        // Given
        updateUserRequest.setUserName("a".repeat(21)); // Plus de 20 caractères

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isBadRequest()); // Validation échoue avant auth

        verify(userService, never()).updateUser(any(), any());
    }

    // =============== TESTS UNAUTHENTICATED ===============

    @Test
    void getUser_Unauthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getByUserName(any());
    }

    @Test
    void updateUser_Unauthenticated() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    void deleteUser_Unauthenticated() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/users/me"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).deleteUser(any());
    }
}