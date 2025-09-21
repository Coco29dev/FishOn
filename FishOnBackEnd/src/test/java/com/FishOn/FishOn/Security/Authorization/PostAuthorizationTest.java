package com.FishOn.FishOn.Security.Authorization;

import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.Config.SecurityConfig;
import com.FishOn.FishOn.DTO.Post.PostUpdateDTO;
import com.FishOn.FishOn.Exception.FishOnException.*;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.PostService;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *  TESTS D'AUTORISATION IDOR - PUBLICATIONS
 *
 * Ces tests vérifient que les utilisateurs ne peuvent pas :
 * - Modifier les publications d'autres utilisateurs
 * - Supprimer les publications d'autres utilisateurs
 * - Accéder aux ressources sans autorisation
 *
 *  VULNÉRABILITÉ IDOR : Insecure Direct Object Reference
 * Un utilisateur malveillant pourrait tenter de modifier/supprimer
 * des publications en changeant simplement l'ID dans l'URL
 */
@WebMvcTest(controllers = com.FishOn.FishOn.Controller.PostController.class)
@Import(SecurityConfig.class)
class PostAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    private ObjectMapper objectMapper;

    // ========== DONNÉES TEST ==========
    private UUID legitimateUserId;
    private UUID maliciousUserId;
    private UUID targetPostId;

    private UserModel legitimateUser;
    private UserModel maliciousUser;
    private PostModel targetPost;

    private CustomUserDetails legitimateUserDetails;
    private CustomUserDetails maliciousUserDetails;

    private PostUpdateDTO updateRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        // ========== IDs DE TEST ==========
        legitimateUserId = UUID.randomUUID();
        maliciousUserId = UUID.randomUUID();
        targetPostId = UUID.randomUUID();

        // ========== UTILISATEUR LÉGITIME (propriétaire du post) ==========
        legitimateUser = new UserModel(
                "fishOwner",
                "owner@fishon.com",
                "Légal",
                "Owner",
                30,
                "hashedPassword",
                "owner.jpg"
        );
        legitimateUser.setId(legitimateUserId);
        legitimateUserDetails = new CustomUserDetails(legitimateUser);

        // ========== UTILISATEUR MALVEILLANT (tente IDOR) ==========
        maliciousUser = new UserModel(
                "hacker",
                "hacker@evil.com",
                "Malicious",
                "Hacker",
                25,
                "hashedPassword",
                "hacker.jpg"
        );
        maliciousUser.setId(maliciousUserId);
        maliciousUserDetails = new CustomUserDetails(maliciousUser);

        // ========== POST CIBLE (appartient à legitimateUser) ==========
        targetPost = new PostModel(
                "Ma belle carpe secrète",
                "Spot secret que je ne veux pas révéler",
                "Carpe",
                "secret-spot.jpg"
        );
        targetPost.setId(targetPostId);
        targetPost.setUser(legitimateUser);
        targetPost.setCreatedAt(LocalDateTime.now().minusHours(2));
        targetPost.setUpdatedAt(LocalDateTime.now().minusHours(2));

        // ========== DONNÉES DE MODIFICATION ==========
        updateRequest = new PostUpdateDTO(
                "Post hacké !",
                "J'ai piraté ce post !",
                "Poisson Trojan",
                "hacked.jpg",
                null, null, null, null
        );
    }

    // =============== TESTS MODIFICATION POST (PUT) ===============

    @Test
    @DisplayName(" IDOR Protection - Utilisateur malveillant tente de modifier le post d'autrui")
    void updatePost_MaliciousUser_ShouldBeBlocked() throws Exception {
        // ARRANGE - Configuration du service pour lever l'exception d'autorisation
        when(postService.updatePost(eq(maliciousUserId), eq(targetPostId), any(PostModel.class)))
                .thenThrow(new UnauthorizedModificationPost());

        // ACT & ASSERT - Tentative IDOR bloquée
        mockMvc.perform(put("/api/posts/{postId}", targetPostId)
                        .with(user(maliciousUserDetails)) //  Se connecte avec le compte malveillant
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden()) //  403 Forbidden attendu
                .andExpect(content().string("N'est pas autorisé à modifier cette publication"));

        // Vérification que le service a bien été appelé avec l'ID malveillant
        verify(postService).updatePost(eq(maliciousUserId), eq(targetPostId), any(PostModel.class));
    }

    @Test
    @DisplayName(" Autorisation Valide - Propriétaire légitime modifie son propre post")
    void updatePost_LegitimateOwner_ShouldSucceed() throws Exception {
        // ARRANGE - Le propriétaire légitime peut modifier son post
        PostModel updatedPost = new PostModel(
                updateRequest.getTitle(),
                updateRequest.getDescription(),
                updateRequest.getFishName(),
                updateRequest.getPhotoUrl()
        );
        updatedPost.setId(targetPostId);
        updatedPost.setUser(legitimateUser);
        updatedPost.setCreatedAt(targetPost.getCreatedAt());
        updatedPost.setUpdatedAt(LocalDateTime.now());

        when(postService.updatePost(eq(legitimateUserId), eq(targetPostId), any(PostModel.class)))
                .thenReturn(updatedPost);

        // ACT & ASSERT - Modification autorisée
        mockMvc.perform(put("/api/posts/{postId}", targetPostId)
                        .with(user(legitimateUserDetails)) //  Se connecte avec le vrai propriétaire
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) //  200 OK attendu
                .andExpect(jsonPath("$.title").value(updateRequest.getTitle()))
                .andExpect(jsonPath("$.userName").value("fishOwner"));

        verify(postService).updatePost(eq(legitimateUserId), eq(targetPostId), any(PostModel.class));
    }

    // =============== TESTS SUPPRESSION POST (DELETE) ===============

    @Test
    @DisplayName(" IDOR Protection - Utilisateur malveillant tente de supprimer le post d'autrui")
    void deletePost_MaliciousUser_ShouldBeBlocked() throws Exception {
        // ARRANGE - Configuration du service pour lever l'exception d'autorisation
        doThrow(new UnauthorizedModificationPost())
                .when(postService).deletePost(maliciousUserId, targetPostId);

        // ACT & ASSERT - Tentative de suppression IDOR bloquée
        mockMvc.perform(delete("/api/posts/{postId}", targetPostId)
                        .with(user(maliciousUserDetails)) //  Utilisateur malveillant
                        .with(csrf()))
                .andExpect(status().isForbidden()) //  403 Forbidden
                .andExpect(content().string("N'est pas autorisé à modifier cette publication"));

        verify(postService).deletePost(maliciousUserId, targetPostId);
    }

    @Test
    @DisplayName(" Autorisation Valide - Propriétaire légitime supprime son propre post")
    void deletePost_LegitimateOwner_ShouldSucceed() throws Exception {
        // ARRANGE - Le propriétaire peut supprimer son post
        doNothing().when(postService).deletePost(legitimateUserId, targetPostId);

        // ACT & ASSERT - Suppression autorisée
        mockMvc.perform(delete("/api/posts/{postId}", targetPostId)
                        .with(user(legitimateUserDetails)) // Vrai propriétaire
                        .with(csrf()))
                .andExpect(status().isOk()) //  200 OK
                .andExpect(content().string("Publication supprimé"));

        verify(postService).deletePost(legitimateUserId, targetPostId);
    }

    // =============== TESTS POST INEXISTANT ===============

    @Test
    @DisplayName(" Sécurité - Tentative d'accès à un post inexistant")
    void updatePost_NonExistentPost_ShouldReturn404() throws Exception {
        // ARRANGE
        UUID nonExistentPostId = UUID.randomUUID();
        when(postService.updatePost(eq(legitimateUserId), eq(nonExistentPostId), any(PostModel.class)))
                .thenThrow(new PostNotFoundById(nonExistentPostId));

        // ACT & ASSERT
        mockMvc.perform(put("/api/posts/{postId}", nonExistentPostId)
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound()) //  404 Not Found
                .andExpect(content().string("La publication n'existe pas"));

        verify(postService).updatePost(eq(legitimateUserId), eq(nonExistentPostId), any(PostModel.class));
    }

    // =============== TESTS SANS AUTHENTIFICATION ===============

    @Test
    @DisplayName(" Sécurité - Tentative de modification sans authentification")
    void updatePost_Unauthenticated_ShouldReturn401() throws Exception {
        // ACT & ASSERT - Pas d'utilisateur connecté
        mockMvc.perform(put("/api/posts/{postId}", targetPostId)
                        .with(csrf()) // CSRF mais pas d'authentification
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized()); //  401 Unauthorized

        // Aucun appel au service ne devrait être fait
        verifyNoInteractions(postService);
    }

    @Test
    @DisplayName(" Sécurité - Tentative de suppression sans authentification")
    void deletePost_Unauthenticated_ShouldReturn401() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(delete("/api/posts/{postId}", targetPostId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); //  401 Unauthorized

        verifyNoInteractions(postService);
    }

    // =============== TESTS SCÉNARIOS AVANCÉS ===============

    @Test
    @DisplayName("Test d'énumération - Tentative d'accès à plusieurs posts d'autrui")
    void updateMultiplePosts_MaliciousUser_AllShouldBeBlocked() throws Exception {
        // ARRANGE - Simulation de plusieurs posts cibles
        UUID[] targetPostIds = {
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        };

        for (UUID postId : targetPostIds) {
            when(postService.updatePost(eq(maliciousUserId), eq(postId), any(PostModel.class)))
                    .thenThrow(new UnauthorizedModificationPost());
        }

        // ACT & ASSERT - Tous les accès doivent être bloqués
        for (UUID postId : targetPostIds) {
            mockMvc.perform(put("/api/posts/{postId}", postId)
                            .with(user(maliciousUserDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden());
        }

        // Vérification que toutes les tentatives ont été bloquées
        for (UUID postId : targetPostIds) {
            verify(postService).updatePost(eq(maliciousUserId), eq(postId), any(PostModel.class));
        }
    }

    @Test
    @DisplayName(" Sécurité Lecture - Les posts d'autrui sont visibles mais non modifiables")
    void getPosts_MaliciousUser_CanViewButNotModify() throws Exception {
        // ARRANGE - L'utilisateur peut voir les posts (lecture publique)
        List<PostModel> allPosts = Arrays.asList(targetPost);
        when(postService.getAll()).thenReturn(allPosts);

        // ACT & ASSERT - Lecture autorisée (feed public)
        mockMvc.perform(get("/api/posts/feed")
                        .with(user(maliciousUserDetails)))
                .andExpect(status().isOk()) // Lecture OK
                .andExpect(jsonPath("$[0].title").value("Ma belle carpe secrète"))
                .andExpect(jsonPath("$[0].userName").value("fishOwner")); // Propriétaire visible

        verify(postService).getAll();

        // Mais modification toujours bloquée
        when(postService.updatePost(eq(maliciousUserId), eq(targetPostId), any(PostModel.class)))
                .thenThrow(new UnauthorizedModificationPost());

        mockMvc.perform(put("/api/posts/{postId}", targetPostId)
                        .with(user(maliciousUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden()); //  Modification bloquée
    }
}