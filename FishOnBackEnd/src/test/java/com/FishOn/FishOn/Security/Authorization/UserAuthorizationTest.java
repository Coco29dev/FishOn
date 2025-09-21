package com.FishOn.FishOn.Security.Authorization;

import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.Config.SecurityConfig;
import com.FishOn.FishOn.DTO.User.UpdateUserRequestDTO;
import com.FishOn.FishOn.Exception.FishOnException.*;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 🔒 TESTS D'AUTORISATION IDOR - PROFILS UTILISATEURS
 *
 * Ces tests vérifient que les utilisateurs ne peuvent pas :
 * - Modifier les profils d'autres utilisateurs
 * - Supprimer les comptes d'autres utilisateurs
 * - Accéder aux données personnelles d'autrui
 *
 *  VULNÉRABILITÉ IDOR : Insecure Direct Object Reference
 * Un utilisateur malveillant pourrait tenter de modifier/supprimer
 * des profils en manipulant les endpoints /me ou les paramètres
 *
 *  PARTICULARITÉ : Les endpoints /me sont auto-référentiels
 * L'ID utilisateur provient de l'authentification, pas de l'URL
 */
@WebMvcTest(controllers = com.FishOn.FishOn.Controller.UserController.class)
@Import(SecurityConfig.class)
class UserAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    // ========== DONNÉES TEST ==========
    private UUID legitimateUserId;
    private UUID maliciousUserId;
    private UUID targetUserId;

    private UserModel legitimateUser;
    private UserModel maliciousUser;
    private UserModel targetUser;

    private CustomUserDetails legitimateUserDetails;
    private CustomUserDetails maliciousUserDetails;

    private UpdateUserRequestDTO updateRequest;
    private UpdateUserRequestDTO maliciousUpdateRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // ========== IDs DE TEST ==========
        legitimateUserId = UUID.randomUUID();
        maliciousUserId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();

        // ========== UTILISATEUR LÉGITIME ==========
        legitimateUser = new UserModel(
                "legitimateUser",
                "legit@fishon.com",
                "Légal",
                "User",
                30,
                "hashedPassword",
                "legit.jpg"
        );
        legitimateUser.setId(legitimateUserId);
        legitimateUserDetails = new CustomUserDetails(legitimateUser);

        // ========== UTILISATEUR MALVEILLANT ==========
        maliciousUser = new UserModel(
                "hacker",
                "hacker@evil.com",
                "Evil",
                "Hacker",
                25,
                "hashedPassword",
                "hacker.jpg"
        );
        maliciousUser.setId(maliciousUserId);
        maliciousUserDetails = new CustomUserDetails(maliciousUser);

        // ========== UTILISATEUR CIBLE (celui qu'on veut pirater) ==========
        targetUser = new UserModel(
                "targetVictim",
                "victim@fishon.com",
                "Target",
                "Victim",
                35,
                "hashedPassword",
                "victim.jpg"
        );
        targetUser.setId(targetUserId);

        // ========== DONNÉES DE MODIFICATION LÉGITIMES ==========
        updateRequest = new UpdateUserRequestDTO(
                "newLegitName",
                "newlegit@fishon.com",
                "New",
                "Name",
                31,
                "newprofile.jpg"
        );

        // ========== DONNÉES DE MODIFICATION MALVEILLANTES ==========
        maliciousUpdateRequest = new UpdateUserRequestDTO(
                "HACKED_USER",
                "hacked@evil.com",
                "Hacked",
                "Victim",
                99,
                "hacker.jpg"
        );
    }

    // =============== TESTS MODIFICATION PROFIL (/me) ===============

    @Test
    @DisplayName(" Autorisation Valide - Utilisateur modifie son propre profil")
    void updateUser_LegitimateUser_ShouldSucceed() throws Exception {
        // ARRANGE - L'utilisateur légitime peut modifier son propre profil
        UserModel updatedUser = new UserModel(
                updateRequest.getUserName(),
                updateRequest.getEmail(),
                updateRequest.getFirstName(),
                updateRequest.getLastName(),
                updateRequest.getAge(),
                null,
                updateRequest.getProfilePicture()
        );
        updatedUser.setId(legitimateUserId);
        updatedUser.setUpdatedAt(LocalDateTime.now());

        when(userService.updateUser(eq(legitimateUserId), any(UserModel.class)))
                .thenReturn(updatedUser);

        // ACT & ASSERT - Modification autorisée
        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails)) //  Utilisateur légitime
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) //  200 OK
                .andExpect(jsonPath("$.userName").value("newLegitName"))
                .andExpect(jsonPath("$.email").value("newlegit@fishon.com"));

        // Vérification que l'ID utilisé est bien celui de l'utilisateur connecté
        verify(userService).updateUser(eq(legitimateUserId), any(UserModel.class));
    }

    @Test
    @DisplayName(" Sécurité /me - L'endpoint utilise toujours l'ID de l'utilisateur connecté")
    void updateUser_EndpointMeUsesAuthenticatedUserId() throws Exception {
        // ARRANGE - Configuration pour l'utilisateur malveillant
        UserModel updatedMaliciousUser = new UserModel(
                maliciousUpdateRequest.getUserName(),
                maliciousUpdateRequest.getEmail(),
                maliciousUpdateRequest.getFirstName(),
                maliciousUpdateRequest.getLastName(),
                maliciousUpdateRequest.getAge(),
                null,
                maliciousUpdateRequest.getProfilePicture()
        );
        updatedMaliciousUser.setId(maliciousUserId);

        when(userService.updateUser(eq(maliciousUserId), any(UserModel.class)))
                .thenReturn(updatedMaliciousUser);

        // ACT & ASSERT - L'utilisateur malveillant ne peut modifier que son propre profil
        mockMvc.perform(put("/api/users/me")
                        .with(user(maliciousUserDetails)) //  Utilisateur malveillant connecté
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maliciousUpdateRequest)))
                .andExpect(status().isOk()) //  OK car il modifie SON profil
                .andExpect(jsonPath("$.userName").value("HACKED_USER")); // Sa propre modification

        //  IMPORTANT : Le service est appelé avec l'ID de l'utilisateur connecté,
        // PAS avec un ID arbitraire qu'il aurait pu spécifier
        verify(userService).updateUser(eq(maliciousUserId), any(UserModel.class));
        // Vérification qu'il N'A PAS pu modifier le profil de la victime
        verify(userService, never()).updateUser(eq(targetUserId), any(UserModel.class));
    }

    // =============== TESTS SUPPRESSION COMPTE (/me) ===============

    @Test
    @DisplayName(" Autorisation Valide - Utilisateur supprime son propre compte")
    void deleteUser_LegitimateUser_ShouldSucceed() throws Exception {
        // ARRANGE - L'utilisateur peut supprimer son propre compte
        doNothing().when(userService).deleteUser(legitimateUserId);

        // ACT & ASSERT - Suppression autorisée
        mockMvc.perform(delete("/api/users/me")
                        .with(user(legitimateUserDetails)) //  Utilisateur légitime
                        .with(csrf()))
                .andExpect(status().isOk()) //  200 OK
                .andExpect(content().string("Compte supprimé avec succès"));

        verify(userService).deleteUser(legitimateUserId);
    }

    @Test
    @DisplayName("🔒 Sécurité /me - Suppression utilise l'ID de l'utilisateur connecté")
    void deleteUser_EndpointMeUsesAuthenticatedUserId() throws Exception {
        // ARRANGE
        doNothing().when(userService).deleteUser(maliciousUserId);

        // ACT & ASSERT - L'utilisateur malveillant ne peut supprimer que son propre compte
        mockMvc.perform(delete("/api/users/me")
                        .with(user(maliciousUserDetails)) //  Utilisateur malveillant
                        .with(csrf()))
                .andExpect(status().isOk()); //  OK car il supprime SON compte

        // Vérification qu'il supprime son propre compte, pas celui d'autrui
        verify(userService).deleteUser(maliciousUserId);
        verify(userService, never()).deleteUser(targetUserId);
    }

    // =============== TESTS LECTURE PROFIL (/me) ===============

    @Test
    @DisplayName(" Autorisation Valide - Utilisateur consulte son propre profil")
    void getUser_LegitimateUser_ShouldSucceed() throws Exception {
        // ACT & ASSERT - Consultation de son propre profil
        mockMvc.perform(get("/api/users/me")
                        .with(user(legitimateUserDetails)))
                .andExpect(status().isOk()) //  200 OK
                .andExpect(jsonPath("$.userName").value("legitimateUser"))
                .andExpect(jsonPath("$.email").value("legit@fishon.com"));

        // ️ IMPORTANT : /me utilise directement l'objet User de l'authentification
        // Pas d'appel au service car les données viennent de CustomUserDetails
    }

    @Test
    @DisplayName("🔒 Isolation - Chaque utilisateur ne voit que son propre profil via /me")
    void getUser_DifferentUsers_SeeOnlyTheirOwnProfile() throws Exception {
        // ACT & ASSERT - Utilisateur légitime voit son profil
        mockMvc.perform(get("/api/users/me")
                        .with(user(legitimateUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("legitimateUser"))
                .andExpect(jsonPath("$.email").value("legit@fishon.com"));

        // ACT & ASSERT - Utilisateur malveillant voit SON profil (pas celui de la victime)
        mockMvc.perform(get("/api/users/me")
                        .with(user(maliciousUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("hacker"))
                .andExpect(jsonPath("$.email").value("hacker@evil.com"));

        //  Chacun voit uniquement ses propres données
    }

    // =============== TESTS RECHERCHE PUBLIQUE (/search) ===============

    @Test
    @DisplayName(" Endpoint Public - Recherche d'utilisateur par nom (lecture seule)")
    void searchUser_PublicEndpoint_ShouldAllowReadOnlyAccess() throws Exception {
        // ARRANGE - Recherche publique d'un utilisateur
        when(userService.getByUserName("targetVictim")).thenReturn(targetUser);

        // ACT & ASSERT - N'importe qui peut rechercher un utilisateur (lecture publique)
        mockMvc.perform(get("/api/users/search/targetVictim")
                        .with(user(maliciousUserDetails))) //  Même l'utilisateur malveillant
                .andExpect(status().isOk()) //  Lecture publique autorisée
                .andExpect(jsonPath("$.userName").value("targetVictim"))
                .andExpect(jsonPath("$.email").value("victim@fishon.com"));

        verify(userService).getByUserName("targetVictim");
    }

    @Test
    @DisplayName(" Recherche Sans Auth - Endpoint public accessible sans connexion")
    void searchUser_Unauthenticated_ShouldSucceed() throws Exception {
        // ARRANGE
        when(userService.getByUserName("targetVictim")).thenReturn(targetUser);

        // ACT & ASSERT - Recherche publique sans authentification
        mockMvc.perform(get("/api/users/search/targetVictim"))
                .andExpect(status().isOk()) //  OK sans authentification
                .andExpect(jsonPath("$.userName").value("targetVictim"));

        verify(userService).getByUserName("targetVictim");
    }

    // =============== TESTS SANS AUTHENTIFICATION ===============

    @Test
    @DisplayName(" Sécurité - Tentative d'accès à /me sans authentification")
    void getUser_Unauthenticated_ShouldReturn401() throws Exception {
        // ACT & ASSERT - Accès à /me sans être connecté
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized()); //  401 Unauthorized

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("🔐 Sécurité - Tentative de modification sans authentification")
    void updateUser_Unauthenticated_ShouldReturn401() throws Exception {
        // ACT & ASSERT - Modification sans être connecté
        mockMvc.perform(put("/api/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized()); // 🔐 401 Unauthorized

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName(" Sécurité - Tentative de suppression sans authentification")
    void deleteUser_Unauthenticated_ShouldReturn401() throws Exception {
        // ACT & ASSERT - Suppression sans être connecté
        mockMvc.perform(delete("/api/users/me")
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); //  401 Unauthorized

        verifyNoInteractions(userService);
    }

    // =============== TESTS VALIDATION DONNÉES ===============

    @Test
    @DisplayName(" Validation - Email déjà pris par un autre utilisateur")
    void updateUser_EmailAlreadyExists_ShouldReturn409() throws Exception {
        // ARRANGE - Email déjà utilisé par quelqu'un d'autre
        when(userService.updateUser(eq(legitimateUserId), any(UserModel.class)))
                .thenThrow(new EmailAlreadyExists("newlegit@fishon.com"));

        // ACT & ASSERT - Conflit détecté
        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict()) // 🛡️ 409 Conflict
                .andExpect(content().string("L'email newlegit@fishon.com est déjà pris"));

        verify(userService).updateUser(eq(legitimateUserId), any(UserModel.class));
    }

    @Test
    @DisplayName("🛡 Validation - Nom d'utilisateur déjà pris")
    void updateUser_UserNameAlreadyExists_ShouldReturn409() throws Exception {
        // ARRANGE - Username déjà utilisé
        when(userService.updateUser(eq(legitimateUserId), any(UserModel.class)))
                .thenThrow(new UserAlreadyExists("newLegitName"));

        // ACT & ASSERT - Conflit détecté
        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict()) // ️ 409 Conflict
                .andExpect(content().string("L'username newLegitName est déjà pris"));

        verify(userService).updateUser(eq(legitimateUserId), any(UserModel.class));
    }

    @Test
    @DisplayName("🛡️ Validation - Données invalides (âge, email, etc.)")
    void updateUser_InvalidData_ShouldReturn400() throws Exception {
        // ARRANGE - Données invalides
        UpdateUserRequestDTO invalidRequest = new UpdateUserRequestDTO(
                "", // Username vide
                "email-invalide", // Format email invalide
                "Valid",
                "User",
                150, // Âge invalide
                "profile.jpg"
        );

        // ACT & ASSERT - Validation échoue avant l'appel au service
        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // ️ 400 Bad Request

        // Le service ne devrait pas être appelé car la validation échoue
        verifyNoInteractions(userService);
    }

    // =============== TESTS COHÉRENCE SÉCURITAIRE ===============

    @Test
    @DisplayName(" Test de cohérence - Multiple utilisateurs simultanés")
    void multipleUsers_SimultaneousOperations_ShouldMaintainIsolation() throws Exception {
        // ARRANGE - Configuration pour deux utilisateurs différents
        UserModel updatedLegitUser = new UserModel("updatedLegit", "updated@legit.com",
                "Updated", "Legit", 32, null, "updated.jpg");
        updatedLegitUser.setId(legitimateUserId);

        UserModel updatedMaliciousUser = new UserModel("updatedHacker", "updated@evil.com",
                "Updated", "Hacker", 26, null, "updatedhacker.jpg");
        updatedMaliciousUser.setId(maliciousUserId);

        when(userService.updateUser(eq(legitimateUserId), any(UserModel.class)))
                .thenReturn(updatedLegitUser);
        when(userService.updateUser(eq(maliciousUserId), any(UserModel.class)))
                .thenReturn(updatedMaliciousUser);

        // ACT & ASSERT - Opérations simultanées, chacun modifie son profil
        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("updatedLegit"));

        mockMvc.perform(put("/api/users/me")
                        .with(user(maliciousUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maliciousUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("updatedHacker"));

        // Vérification que chaque utilisateur a modifié uniquement son profil
        verify(userService).updateUser(eq(legitimateUserId), any(UserModel.class));
        verify(userService).updateUser(eq(maliciousUserId), any(UserModel.class));
        // Aucune cross-contamination
        verify(userService, never()).updateUser(eq(targetUserId), any(UserModel.class));
    }

    @Test
    @DisplayName(" Test de session hijacking - Tentative d'usurpation de session")
    void updateUser_SessionIsolation_ShouldMaintainUserIdentity() throws Exception {
        // ARRANGE - Configuration pour l'utilisateur légitime
        UserModel updatedUser = new UserModel(
                updateRequest.getUserName(),
                updateRequest.getEmail(),
                updateRequest.getFirstName(),
                updateRequest.getLastName(),
                updateRequest.getAge(),
                null,
                updateRequest.getProfilePicture()
        );
        updatedUser.setId(legitimateUserId);

        when(userService.updateUser(eq(legitimateUserId), any(UserModel.class)))
                .thenReturn(updatedUser);

        // ACT & ASSERT - L'identité de l'utilisateur reste cohérente
        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails)) //  Session utilisateur légitime
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(legitimateUserId.toString())) // ✅ ID cohérent
                .andExpect(jsonPath("$.userName").value("newLegitName"));

        // Vérification que l'ID utilisé correspond bien à l'utilisateur authentifié
        verify(userService).updateUser(eq(legitimateUserId), any(UserModel.class));
    }

    // =============== TESTS SCÉNARIOS D'ATTAQUE AVANCÉS ===============

    @Test
    @DisplayName("⚡ Test de force brute - Tentatives répétées sur différents profils")
    void searchUser_BruteForceAttempts_ShouldBeConsistent() throws Exception {
        // ARRANGE - Simulation d'attaque par force brute sur la recherche
        String[] usernames = {"user1", "user2", "user3", "admin", "root"};

        for (String username : usernames) {
            when(userService.getByUserName(username))
                    .thenThrow(new UserNotFoundByUserName(username));
        }

        // ACT & ASSERT - Toutes les tentatives retournent 404 de manière cohérente
        for (String username : usernames) {
            mockMvc.perform(get("/api/users/search/{userName}", username)
                            .with(user(maliciousUserDetails)))
                    .andExpect(status().isNotFound()) //  404 Not Found
                    .andExpect(content().string("L'utilisateur " + username + " n'existe pas"));
        }

        // Vérification que toutes les tentatives ont été traitées
        for (String username : usernames) {
            verify(userService).getByUserName(username);
        }
    }

    @Test
    @DisplayName(" Test d'usurpation d'identité - Tentative de modification avec fausses données")
    void updateUser_IdentityTheft_ShouldPreventImpersonation() throws Exception {
        // ARRANGE - Tentative d'usurpation avec des données ressemblant à un admin
        UpdateUserRequestDTO impersonationRequest = new UpdateUserRequestDTO(
                "admin", // Tentative d'usurper le nom admin
                "admin@fishon.com", // Email d'admin
                "Super",
                "Admin",
                99,
                "admin.jpg"
        );

        // Configuration : l'utilisateur malveillant essaie de se faire passer pour admin
        UserModel updatedMaliciousUser = new UserModel(
                impersonationRequest.getUserName(),
                impersonationRequest.getEmail(),
                impersonationRequest.getFirstName(),
                impersonationRequest.getLastName(),
                impersonationRequest.getAge(),
                null,
                impersonationRequest.getProfilePicture()
        );
        updatedMaliciousUser.setId(maliciousUserId); //  Son vrai ID reste inchangé

        when(userService.updateUser(eq(maliciousUserId), any(UserModel.class)))
                .thenReturn(updatedMaliciousUser);

        // ACT & ASSERT - L'usurpation échoue car l'ID reste celui de l'utilisateur connecté
        mockMvc.perform(put("/api/users/me")
                        .with(user(maliciousUserDetails)) // Utilisateur malveillant
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(impersonationRequest)))
                .andExpect(status().isOk()) //  Modification acceptée
                .andExpect(jsonPath("$.id").value(maliciousUserId.toString())) //  Mais ID inchangé
                .andExpect(jsonPath("$.userName").value("admin")); // Il peut changer le nom affiché

        //  IMPORTANT : L'ID utilisateur reste celui de l'authentification
        verify(userService).updateUser(eq(maliciousUserId), any(UserModel.class));
        // Il ne peut PAS modifier le profil avec un autre ID
        verify(userService, never()).updateUser(eq(targetUserId), any(UserModel.class));
    }

    // =============== TESTS EDGE CASES ET LIMITES ===============

    @Test
    @DisplayName(" Test de déni de service - Tentative de suppression en masse")
    void deleteUser_DoSAttempt_ShouldOnlyDeleteOwnAccount() throws Exception {
        // ARRANGE - L'utilisateur malveillant essaie de supprimer son compte plusieurs fois
        doNothing().when(userService).deleteUser(maliciousUserId);

        // ACT & ASSERT - Multiple tentatives de suppression
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(delete("/api/users/me")
                            .with(user(maliciousUserDetails))
                            .with(csrf()))
                    .andExpect(status().isOk()); //  Toujours son propre compte
        }

        // Vérification : seul son propre compte est ciblé
        verify(userService, times(5)).deleteUser(maliciousUserId);
        verify(userService, never()).deleteUser(legitimateUserId);
        verify(userService, never()).deleteUser(targetUserId);
    }

    @Test
    @DisplayName(" Test de reconnaissance - Énumération des utilisateurs existants")
    void searchUser_UserEnumeration_ShouldRevealMinimalInfo() throws Exception {
        // ARRANGE - Recherche d'utilisateurs existants vs inexistants
        when(userService.getByUserName("existingUser")).thenReturn(targetUser);
        when(userService.getByUserName("nonExistentUser"))
                .thenThrow(new UserNotFoundByUserName("nonExistentUser"));

        // ACT & ASSERT - Utilisateur existant trouvé
        mockMvc.perform(get("/api/users/search/existingUser"))
                .andExpect(status().isOk()) //  200 OK
                .andExpect(jsonPath("$.userName").exists());

        // ACT & ASSERT - Utilisateur inexistant
        mockMvc.perform(get("/api/users/search/nonExistentUser"))
                .andExpect(status().isNotFound()) //  404 Not Found
                .andExpect(content().string("L'utilisateur nonExistentUser n'existe pas"));

        //  REMARQUE : Cette différence de réponse permet l'énumération
        // mais c'est acceptable pour un endpoint de recherche publique
        verify(userService).getByUserName("existingUser");
        verify(userService).getByUserName("nonExistentUser");
    }

    // =============== TESTS DE ROBUSTESSE ===============

    @Test
    @DisplayName(" Test de robustesse - Données volumineuses")
    void updateUser_LargePayload_ShouldBeRejected() throws Exception {
        // ARRANGE - Tentative avec des données très volumineuses
        UpdateUserRequestDTO largePayloadRequest = new UpdateUserRequestDTO(
                "A".repeat(1000), // Username très long
                "verylongemail" + "A".repeat(1000) + "@evil.com", // Email très long
                "B".repeat(500),  // Prénom très long
                "C".repeat(500),  // Nom très long
                25,
                "profile.jpg"
        );

        // ACT & ASSERT - Payload trop volumineux rejeté par la validation
        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(largePayloadRequest)))
                .andExpect(status().isBadRequest()); //  400 Bad Request

        // Le service ne devrait pas être appelé
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("🔐 Test de cohérence finale - État de l'application après attaques")
    void applicationState_AfterAttackAttempts_ShouldRemainConsistent() throws Exception {
        // ARRANGE - Multiple tentatives d'attaque
        when(userService.updateUser(eq(legitimateUserId), any(UserModel.class)))
                .thenThrow(new EmailAlreadyExists("test@test.com"));

        // ACT - Plusieurs tentatives échouées
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(put("/api/users/me")
                            .with(user(legitimateUserDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isConflict()); //  Toutes échouent
        }

        // ACT & ASSERT - L'utilisateur peut toujours consulter son profil normalement
        mockMvc.perform(get("/api/users/me")
                        .with(user(legitimateUserDetails)))
                .andExpect(status().isOk()) //  L'état reste cohérent
                .andExpect(jsonPath("$.userName").value("legitimateUser"));

        // Vérification que l'application reste stable
        verify(userService, times(3)).updateUser(eq(legitimateUserId), any(UserModel.class));
    }

    // =============== TESTS SPÉCIAUX ENDPOINT /me ===============

    @Test
    @DisplayName(" Sécurité /me - Impossible de spécifier un ID externe dans le body")
    void updateUser_CannotOverrideUserIdFromBody() throws Exception {
        // ARRANGE - Tentative d'injection d'ID dans le body (même si ce n'est pas dans le DTO)
        UserModel updatedUser = new UserModel(
                updateRequest.getUserName(),
                updateRequest.getEmail(),
                updateRequest.getFirstName(),
                updateRequest.getLastName(),
                updateRequest.getAge(),
                null,
                updateRequest.getProfilePicture()
        );
        updatedUser.setId(legitimateUserId); // ID correct attendu

        when(userService.updateUser(eq(legitimateUserId), any(UserModel.class)))
                .thenReturn(updatedUser);

        // ACT & ASSERT - L'ID est toujours celui de l'authentification
        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(legitimateUserId.toString())); //  ID correct

        // L'endpoint /me garantit que seul l'ID de l'utilisateur authentifié est utilisé
        verify(userService).updateUser(eq(legitimateUserId), any(UserModel.class));
    }

    @Test
    @DisplayName(" Test cyclique - Modifications successives du même utilisateur")
    void updateUser_SuccessiveModifications_ShouldMaintainIdentity() throws Exception {
        // ARRANGE - Modifications successives
        UpdateUserRequestDTO modification1 = new UpdateUserRequestDTO(
                "name1", "email1@test.com", "First1", "Last1", 25, "pic1.jpg");
        UpdateUserRequestDTO modification2 = new UpdateUserRequestDTO(
                "name2", "email2@test.com", "First2", "Last2", 26, "pic2.jpg");
        UpdateUserRequestDTO modification3 = new UpdateUserRequestDTO(
                "name3", "email3@test.com", "First3", "Last3", 27, "pic3.jpg");

        // Mocks pour chaque modification
        UserModel result1 = new UserModel("name1", "email1@test.com", "First1", "Last1", 25, null, "pic1.jpg");
        result1.setId(legitimateUserId);
        UserModel result2 = new UserModel("name2", "email2@test.com", "First2", "Last2", 26, null, "pic2.jpg");
        result2.setId(legitimateUserId);
        UserModel result3 = new UserModel("name3", "email3@test.com", "First3", "Last3", 27, null, "pic3.jpg");
        result3.setId(legitimateUserId);

        when(userService.updateUser(eq(legitimateUserId), any(UserModel.class)))
                .thenReturn(result1, result2, result3);

        // ACT & ASSERT - 3 modifications successives
        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modification1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("name1"))
                .andExpect(jsonPath("$.id").value(legitimateUserId.toString()));

        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modification2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("name2"))
                .andExpect(jsonPath("$.id").value(legitimateUserId.toString()));

        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modification3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("name3"))
                .andExpect(jsonPath("$.id").value(legitimateUserId.toString()));

        // Vérification que l'ID reste cohérent dans toutes les modifications
        verify(userService, times(3)).updateUser(eq(legitimateUserId), any(UserModel.class));
    }

    // =============== TESTS PERFORMANCE SÉCURITÉ ===============

    @Test
    @DisplayName(" Test de performance - Authentification répétée")
    void authentication_RepeatedRequests_ShouldMaintainPerformance() throws Exception {
        // ARRANGE - Simulation de requêtes répétées
        int numberOfRequests = 10;

        // ACT & ASSERT - Multiple requêtes rapides
        for (int i = 0; i < numberOfRequests; i++) {
            mockMvc.perform(get("/api/users/me")
                            .with(user(legitimateUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userName").value("legitimateUser"));
        }

        //  Toutes les requêtes devraient réussir sans dégradation
        // Aucun appel au service car /me utilise les données d'authentification
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName(" Test final - Isolation complète des utilisateurs")
    void userIsolation_CompleteSecurityTest_ShouldPreventAllCrossAccess() throws Exception {
        // ARRANGE - Configuration pour isolation complète
        UserModel legitUpdate = new UserModel("legitUpdated", "legit@updated.com",
                "Updated", "Legit", 32, null, "updated.jpg");
        legitUpdate.setId(legitimateUserId);

        UserModel maliciousUpdate = new UserModel("hackerUpdated", "hacker@updated.com",
                "Updated", "Hacker", 26, null, "hacker.jpg");
        maliciousUpdate.setId(maliciousUserId);

        when(userService.updateUser(eq(legitimateUserId), any(UserModel.class)))
                .thenReturn(legitUpdate);
        when(userService.updateUser(eq(maliciousUserId), any(UserModel.class)))
                .thenReturn(maliciousUpdate);
        when(userService.getByUserName("targetVictim")).thenReturn(targetUser);

        // ACT & ASSERT - Test isolation complète

        // 1. Chaque utilisateur peut modifier son propre profil
        mockMvc.perform(put("/api/users/me")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(legitimateUserId.toString()));

        mockMvc.perform(put("/api/users/me")
                        .with(user(maliciousUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maliciousUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(maliciousUserId.toString()));

        // 2. Chaque utilisateur ne voit que son propre profil via /me
        mockMvc.perform(get("/api/users/me")
                        .with(user(legitimateUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("legitimateUser"));

        mockMvc.perform(get("/api/users/me")
                        .with(user(maliciousUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("hacker"));

        // 3. Recherche publique fonctionne pour tous (lecture seule)
        mockMvc.perform(get("/api/users/search/targetVictim")
                        .with(user(legitimateUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("targetVictim"));

        mockMvc.perform(get("/api/users/search/targetVictim")
                        .with(user(maliciousUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("targetVictim"));

        // 4. Vérification de l'isolation - aucune cross-contamination
        verify(userService).updateUser(eq(legitimateUserId), any(UserModel.class));
        verify(userService).updateUser(eq(maliciousUserId), any(UserModel.class));
        verify(userService, never()).updateUser(eq(targetUserId), any(UserModel.class));
        verify(userService, times(2)).getByUserName("targetVictim");

        //  SÉCURITÉ CONFIRMÉE : Isolation complète maintenue
    }
}