package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.Config.SecurityConfig;
import com.FishOn.FishOn.DTO.User.UpdateUserRequestDTO;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.UserService;
import com.FishOn.FishOn.Exception.FishOnException.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test unitaire du UserController avec Spring Security réel et MockMvc
 * Permet de tester :
 *  - les endpoints protégés (/me) avec authentification et CSRF
 *  - les endpoints publics (/search/**)
 *  - le comportement en cas d'exceptions personnalisées
 */
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class) // On importe la config Spring Security pour tests réels
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // Permet de simuler les requêtes HTTP vers le controller

    @MockBean
    private UserService userService; // Service mocké pour ne pas toucher à la vraie base

    private ObjectMapper objectMapper; // Pour sérialiser/désérialiser JSON

    private UserModel mockUser; // Utilisateur simulé
    private CustomUserDetails mockUserDetails; // UserDetails simulé pour Spring Security

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper(); // Initialisation du mapper JSON

        // Création d'un utilisateur factice
        mockUser = new UserModel(
                "fishMaster",
                "fish.master@example.com",
                "John",
                "Doe",
                30,
                "hashedPassword",
                null
        );
        mockUser.setId(UUID.randomUUID()); // ID aléatoire pour simuler la BDD

        // Création d'un CustomUserDetails pour authentification dans les tests
        mockUserDetails = new CustomUserDetails(mockUser);
    }

    // ================= GET /me =================
    @Test
    public void testGetUser_authenticated() throws Exception {
        // Test endpoint protégé avec utilisateur authentifié
        mockMvc.perform(get("/api/users/me")
                        .with(user(mockUserDetails))) // simulation de l'utilisateur connecté
                .andExpect(status().isOk()) // On attend un 200
                .andExpect(jsonPath("$.userName").value("fishMaster")) // Vérifie le JSON retourné
                .andExpect(jsonPath("$.email").value("fish.master@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    public void testGetUser_unauthenticated() throws Exception {
        // Test du même endpoint sans utilisateur -> doit renvoyer 401
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    // ================= PUT /me =================
    @Test
    public void testUpdateUser_authenticated() throws Exception, EmailAlreadyExists, UserAlreadyExists, UserNotFoundById {
        // Création d'un DTO de modification
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO(
                "newUserName",
                "new.email@example.com",
                "Jane",
                "Smith",
                25,
                null
        );

        // Création de l'utilisateur mis à jour pour la réponse simulée
        UserModel updatedUser = new UserModel(
                updateRequest.getUserName(),
                updateRequest.getEmail(),
                updateRequest.getFirstName(),
                updateRequest.getLastName(),
                updateRequest.getAge(),
                null,
                updateRequest.getProfilePicture()
        );
        updatedUser.setId(mockUser.getId()); // Même ID
        updatedUser.setUpdatedAt(java.time.LocalDateTime.now()); // Timestamp mis à jour

        // Simule l'appel au service -> retourne l'utilisateur mis à jour
        when(userService.updateUser(eq(mockUser.getId()), any(UserModel.class)))
                .thenReturn(updatedUser);

        // Appel du endpoint PUT /me avec authentification et CSRF
        mockMvc.perform(put("/api/users/me")
                        .with(user(mockUserDetails)) // utilisateur connecté
                        .with(csrf()) // token CSRF requis pour PUT/DELETE
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))) // corps JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("newUserName"))
                .andExpect(jsonPath("$.email").value("new.email@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    public void testUpdateUser_unauthenticated() throws Exception {
        // Test PUT /me sans utilisateur -> 401
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO(
                "newUserName",
                "new.email@example.com",
                "Jane",
                "Smith",
                25,
                null
        );

        mockMvc.perform(put("/api/users/me")
                        .with(csrf()) // CSRF mais pas d'utilisateur
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }

    // ================= DELETE /me =================
    @Test
    public void testDeleteUser_authenticated() throws Exception, UserNotFoundById {
        // Test suppression compte utilisateur avec authentification
        mockMvc.perform(delete("/api/users/me")
                        .with(user(mockUserDetails))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Compte supprimé avec succès"));
    }

    @Test
    public void testDeleteUser_unauthenticated() throws Exception {
        // Test suppression sans utilisateur -> 401
        mockMvc.perform(delete("/api/users/me")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // ================= GET /search/{userName} =================
    @Test
    public void testGetUserByUserName_publicEndpoint() throws Exception, UserNotFoundByUserName {
        // Simule le service qui retourne un utilisateur pour la recherche
        when(userService.getByUserName("fishMaster")).thenReturn(mockUser);

        // Endpoint public -> pas besoin de user() ni csrf()
        mockMvc.perform(get("/api/users/search/fishMaster"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("fishMaster"))
                .andExpect(jsonPath("$.email").value("fish.master@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.age").value(30));
    }
}
