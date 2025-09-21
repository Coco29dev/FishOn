package com.FishOn.FishOn.Security.Authorization;

import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.Config.SecurityConfig;
import com.FishOn.FishOn.DTO.Comment.CommentUpdateDTO;
import com.FishOn.FishOn.Exception.FishOnException.*;
import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.CommentService;
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
 *  TESTS D'AUTORISATION IDOR - COMMENTAIRES
 *
 * Ces tests vérifient que les utilisateurs ne peuvent pas :
 * - Modifier les commentaires d'autres utilisateurs
 * - Supprimer les commentaires d'autres utilisateurs
 * - Effectuer des opérations non autorisées sur les interactions sociales
 *
 *  VULNÉRABILITÉ IDOR : Insecure Direct Object Reference
 * Un utilisateur malveillant pourrait tenter de modifier/supprimer
 * des commentaires en changeant l'ID dans l'URL
 */
@WebMvcTest(controllers = com.FishOn.FishOn.Controller.CommentController.class)
@Import(SecurityConfig.class)
class CommentAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private ObjectMapper objectMapper;

    // ========== DONNÉES TEST ==========
    private UUID legitimateUserId;
    private UUID maliciousUserId;
    private UUID postOwnerId;
    private UUID targetCommentId;
    private UUID postId;

    private UserModel legitimateUser;
    private UserModel maliciousUser;
    private UserModel postOwner;
    private CommentModel targetComment;
    private PostModel targetPost;

    private CustomUserDetails legitimateUserDetails;
    private CustomUserDetails maliciousUserDetails;
    private CustomUserDetails postOwnerDetails;

    private CommentUpdateDTO updateRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // ========== IDs DE TEST ==========
        legitimateUserId = UUID.randomUUID();
        maliciousUserId = UUID.randomUUID();
        postOwnerId = UUID.randomUUID();
        targetCommentId = UUID.randomUUID();
        postId = UUID.randomUUID();

        // ========== UTILISATEUR LÉGITIME (propriétaire du commentaire) ==========
        legitimateUser = new UserModel(
                "commentOwner",
                "commenter@fishon.com",
                "Légal",
                "Commenter",
                28,
                "hashedPassword",
                "commenter.jpg"
        );
        legitimateUser.setId(legitimateUserId);
        legitimateUserDetails = new CustomUserDetails(legitimateUser);

        // ========== UTILISATEUR MALVEILLANT (tente IDOR) ==========
        maliciousUser = new UserModel(
                "evilCommenter",
                "evil@hacker.com",
                "Evil",
                "Hacker",
                22,
                "hashedPassword",
                "evil.jpg"
        );
        maliciousUser.setId(maliciousUserId);
        maliciousUserDetails = new CustomUserDetails(maliciousUser);

        // ========== PROPRIÉTAIRE DU POST ==========
        postOwner = new UserModel(
                "postOwner",
                "postowner@fishon.com",
                "Post",
                "Owner",
                35,
                "hashedPassword",
                "postowner.jpg"
        );
        postOwner.setId(postOwnerId);
        postOwnerDetails = new CustomUserDetails(postOwner);

        // ========== POST CIBLE ==========
        targetPost = new PostModel(
                "Belle journée de pêche",
                "Superbe session aujourd'hui",
                "Truite",
                "trout.jpg"
        );
        targetPost.setId(postId);
        targetPost.setUser(postOwner);

        // ========== COMMENTAIRE CIBLE (appartient à legitimateUser) ==========
        targetComment = new CommentModel("Super prise ! Bravo !");
        targetComment.setId(targetCommentId);
        targetComment.setUser(legitimateUser); //  Propriétaire légitime
        targetComment.setPost(targetPost);
        targetComment.setCreatedAt(LocalDateTime.now().minusHours(1));
        targetComment.setUpdatedAt(LocalDateTime.now().minusHours(1));

        // ========== DONNÉES DE MODIFICATION ==========
        updateRequest = new CommentUpdateDTO("Commentaire piraté par un hacker !");
    }

    // =============== TESTS MODIFICATION COMMENTAIRE (PUT) ===============

    @Test
    @DisplayName(" IDOR Protection - Utilisateur malveillant tente de modifier le commentaire d'autrui")
    void updateComment_MaliciousUser_ShouldBeBlocked() throws Exception {
        // ARRANGE - Configuration du service pour lever l'exception d'autorisation
        when(commentService.updateComment(eq(targetCommentId), any(CommentModel.class), eq(maliciousUserId)))
                .thenThrow(new UnauthorizedAccess());

        // ACT & ASSERT - Tentative IDOR bloquée
        mockMvc.perform(put("/api/comments/{commentId}", targetCommentId)
                        .with(user(maliciousUserDetails)) //  Se connecte avec le compte malveillant
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden()) //  403 Forbidden attendu
                .andExpect(content().string("N'est pas autorisé à modifier"));

        // Vérification que le service a bien été appelé avec l'ID malveillant
        verify(commentService).updateComment(eq(targetCommentId), any(CommentModel.class), eq(maliciousUserId));
    }

    @Test
    @DisplayName(" Autorisation Valide - Propriétaire légitime modifie son propre commentaire")
    void updateComment_LegitimateOwner_ShouldSucceed() throws Exception {
        // ARRANGE - Le propriétaire légitime peut modifier son commentaire
        CommentModel updatedComment = new CommentModel(updateRequest.getContent());
        updatedComment.setId(targetCommentId);
        updatedComment.setUser(legitimateUser);
        updatedComment.setPost(targetPost);
        updatedComment.setCreatedAt(targetComment.getCreatedAt());
        updatedComment.setUpdatedAt(LocalDateTime.now());

        when(commentService.updateComment(eq(targetCommentId), any(CommentModel.class), eq(legitimateUserId)))
                .thenReturn(updatedComment);

        // ACT & ASSERT - Modification autorisée
        mockMvc.perform(put("/api/comments/{commentId}", targetCommentId)
                        .with(user(legitimateUserDetails)) //  Se connecte avec le vrai propriétaire
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) //  200 OK attendu
                .andExpect(jsonPath("$.content").value(updateRequest.getContent()))
                .andExpect(jsonPath("$.userName").value("commentOwner"));

        verify(commentService).updateComment(eq(targetCommentId), any(CommentModel.class), eq(legitimateUserId));
    }

    // =============== TESTS SUPPRESSION COMMENTAIRE (DELETE) ===============

    @Test
    @DisplayName(" IDOR Protection - Utilisateur malveillant tente de supprimer le commentaire d'autrui")
    void deleteComment_MaliciousUser_ShouldBeBlocked() throws Exception {
        // ARRANGE - Configuration du service pour lever l'exception d'autorisation
        doThrow(new UnauthorizedAccess())
                .when(commentService).deleteComment(targetCommentId, maliciousUserId);

        // ACT & ASSERT - Tentative de suppression IDOR bloquée
        mockMvc.perform(delete("/api/comments/{commentId}", targetCommentId)
                        .with(user(maliciousUserDetails)) //  Utilisateur malveillant
                        .with(csrf()))
                .andExpect(status().isForbidden()) //  403 Forbidden
                .andExpect(content().string("N'est pas autorisé à modifier"));

        verify(commentService).deleteComment(targetCommentId, maliciousUserId);
    }

    @Test
    @DisplayName(" Autorisation Valide - Propriétaire légitime supprime son propre commentaire")
    void deleteComment_LegitimateOwner_ShouldSucceed() throws Exception {
        // ARRANGE - Le propriétaire peut supprimer son commentaire
        doNothing().when(commentService).deleteComment(targetCommentId, legitimateUserId);

        // ACT & ASSERT - Suppression autorisée
        mockMvc.perform(delete("/api/comments/{commentId}", targetCommentId)
                        .with(user(legitimateUserDetails)) //  Vrai propriétaire
                        .with(csrf()))
                .andExpect(status().isOk()) //  200 OK
                .andExpect(content().string("Commentaire supprimé avec succès"));

        verify(commentService).deleteComment(targetCommentId, legitimateUserId);
    }

    // =============== TESTS COMMENTAIRE INEXISTANT ===============

    @Test
    @DisplayName(" Sécurité - Tentative d'accès à un commentaire inexistant")
    void updateComment_NonExistentComment_ShouldReturn404() throws Exception {
        // ARRANGE
        UUID nonExistentCommentId = UUID.randomUUID();
        when(commentService.updateComment(eq(nonExistentCommentId), any(CommentModel.class), eq(legitimateUserId)))
                .thenThrow(new CommentNotFound(nonExistentCommentId));

        // ACT & ASSERT
        mockMvc.perform(put("/api/comments/{commentId}", nonExistentCommentId)
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound()) //  404 Not Found
                .andExpect(content().string("Le commentaire " + nonExistentCommentId + " n'existe pas"));

        verify(commentService).updateComment(eq(nonExistentCommentId), any(CommentModel.class), eq(legitimateUserId));
    }

    // =============== TESTS SANS AUTHENTIFICATION ===============

    @Test
    @DisplayName(" Sécurité - Tentative de modification sans authentification")
    void updateComment_Unauthenticated_ShouldReturn401() throws Exception {
        // ACT & ASSERT - Pas d'utilisateur connecté
        mockMvc.perform(put("/api/comments/{commentId}", targetCommentId)
                        .with(csrf()) // CSRF mais pas d'authentification
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized()); //  401 Unauthorized

        // Aucun appel au service ne devrait être fait
        verifyNoInteractions(commentService);
    }

    @Test
    @DisplayName(" Sécurité - Tentative de suppression sans authentification")
    void deleteComment_Unauthenticated_ShouldReturn401() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(delete("/api/comments/{commentId}", targetCommentId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); //  401 Unauthorized

        verifyNoInteractions(commentService);
    }

    // =============== TESTS LECTURE COMMENTAIRES ===============

    @Test
    @DisplayName(" Sécurité Lecture - Tous les utilisateurs peuvent lire les commentaires publics")
    void getCommentsByPost_AnyAuthenticatedUser_ShouldSucceed() throws Exception {
        // ARRANGE - Commentaires d'un post (lecture publique)
        CommentModel comment1 = new CommentModel("Premier commentaire");
        comment1.setId(UUID.randomUUID());
        comment1.setUser(legitimateUser);
        comment1.setCreatedAt(LocalDateTime.now());
        comment1.setUpdatedAt(LocalDateTime.now());

        CommentModel comment2 = new CommentModel("Deuxième commentaire");
        comment2.setId(UUID.randomUUID());
        comment2.setUser(maliciousUser);
        comment2.setCreatedAt(LocalDateTime.now());
        comment2.setUpdatedAt(LocalDateTime.now());

        List<CommentModel> comments = Arrays.asList(comment1, comment2);
        when(commentService.getByPostId(postId)).thenReturn(comments);

        // ACT & ASSERT - Lecture autorisée pour tous
        mockMvc.perform(get("/api/comments/post/{postId}", postId)
                        .with(user(maliciousUserDetails))) //  Même l'utilisateur malveillant peut lire
                .andExpect(status().isOk()) //  Lecture OK
                .andExpect(jsonPath("$[0].content").value("Premier commentaire"))
                .andExpect(jsonPath("$[1].content").value("Deuxième commentaire"));

        verify(commentService).getByPostId(postId);
    }

    // =============== TESTS SCÉNARIOS AVANCÉS ===============

    @Test
    @DisplayName(" Test d'énumération - Tentative d'accès à plusieurs commentaires d'autrui")
    void updateMultipleComments_MaliciousUser_AllShouldBeBlocked() throws Exception {
        // ARRANGE - Simulation de plusieurs commentaires cibles
        UUID[] targetCommentIds = {
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        };

        for (UUID commentId : targetCommentIds) {
            when(commentService.updateComment(eq(commentId), any(CommentModel.class), eq(maliciousUserId)))
                    .thenThrow(new UnauthorizedAccess());
        }

        // ACT & ASSERT - Tous les accès doivent être bloqués
        for (UUID commentId : targetCommentIds) {
            mockMvc.perform(put("/api/comments/{commentId}", commentId)
                            .with(user(maliciousUserDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden());
        }

        // Vérification que toutes les tentatives ont été bloquées
        for (UUID commentId : targetCommentIds) {
            verify(commentService).updateComment(eq(commentId), any(CommentModel.class), eq(maliciousUserId));
        }
    }

    @Test
    @DisplayName(" Test de substitution d'ID - Tentative de modifier un commentaire en changeant l'ID")
    void updateComment_IDSubstitution_ShouldBeBlocked() throws Exception {
        // ARRANGE - L'utilisateur malveillant tente de remplacer l'ID d'un commentaire
        UUID originalCommentId = UUID.randomUUID(); // Son propre commentaire (hypothétique)
        UUID targetCommentId = UUID.randomUUID();   // Commentaire d'autrui qu'il veut modifier

        when(commentService.updateComment(eq(targetCommentId), any(CommentModel.class), eq(maliciousUserId)))
                .thenThrow(new UnauthorizedAccess());

        // ACT & ASSERT - Substitution d'ID bloquée
        mockMvc.perform(put("/api/comments/{commentId}", targetCommentId) //  ID substitué
                        .with(user(maliciousUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("N'est pas autorisé à modifier"));

        verify(commentService).updateComment(eq(targetCommentId), any(CommentModel.class), eq(maliciousUserId));
    }

    @Test
    @DisplayName(" Cas spécial - Propriétaire du post ne peut pas modifier les commentaires d'autrui")
    void updateComment_PostOwnerTriesToModifyComment_ShouldBeBlocked() throws Exception {
        // ARRANGE - Même le propriétaire du post ne peut pas modifier les commentaires des autres
        when(commentService.updateComment(eq(targetCommentId), any(CommentModel.class), eq(postOwnerId)))
                .thenThrow(new UnauthorizedAccess());

        // ACT & ASSERT - Propriétaire du post ne peut pas modifier les commentaires d'autrui
        mockMvc.perform(put("/api/comments/{commentId}", targetCommentId)
                        .with(user(postOwnerDetails)) //  Propriétaire du post
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden()) //  Même lui est bloqué
                .andExpect(content().string("N'est pas autorisé à modifier"));

        verify(commentService).updateComment(eq(targetCommentId), any(CommentModel.class), eq(postOwnerId));
    }

    // =============== TESTS VALIDATION DONNÉES ===============

    @Test
    @DisplayName("️ Protection - Tentative de modification avec données malveillantes")
    void updateComment_MaliciousContent_ShouldBeValidated() throws Exception {
        // ARRANGE - Contenu malveillant (script XSS, contenu trop long, etc.)
        CommentUpdateDTO maliciousRequest = new CommentUpdateDTO(
                "<script>alert('XSS')</script>" + "A".repeat(1500) // Contenu trop long + XSS
        );

        // ACT & ASSERT - Validation des données (taille maximale dépassée)
        mockMvc.perform(put("/api/comments/{commentId}", targetCommentId)
                        .with(user(legitimateUserDetails)) // Même avec utilisateur légitime
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isBadRequest()); //  400 Bad Request (validation échoue)

        // Le service ne devrait pas être appelé car la validation échoue avant
        verifyNoInteractions(commentService);
    }

    @Test
    @DisplayName(" Test de performance - Tentatives répétées d'IDOR")
    void updateComment_RepeatedIDORAttempts_ShouldBeConsistentlyBlocked() throws Exception {
        // ARRANGE - Simulation d'attaques répétées
        when(commentService.updateComment(eq(targetCommentId), any(CommentModel.class), eq(maliciousUserId)))
                .thenThrow(new UnauthorizedAccess());

        // ACT & ASSERT - 10 tentatives consécutives, toutes bloquées
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(put("/api/comments/{commentId}", targetCommentId)
                            .with(user(maliciousUserDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden()); //  Toujours bloqué
        }

        // Vérification que toutes les tentatives ont été traitées de manière cohérente
        verify(commentService, times(10)).updateComment(eq(targetCommentId), any(CommentModel.class), eq(maliciousUserId));
    }

    // =============== TESTS EDGE CASES ===============

    @Test
    @DisplayName(" Test avec ID null - Gestion des cas limites")
    void updateComment_NullCommentId_ShouldReturn400() throws Exception {
        // ACT & ASSERT - ID null dans l'URL (cas limite)
        mockMvc.perform(put("/api/comments/null")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest()); // 🎯 400 Bad Request

        verifyNoInteractions(commentService);
    }

    @Test
    @DisplayName(" Test avec ID malformé - Gestion des erreurs")
    void updateComment_MalformedCommentId_ShouldReturn400() throws Exception {
        // ACT & ASSERT - ID malformé dans l'URL
        mockMvc.perform(put("/api/comments/not-a-valid-uuid")
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest()); //  400 Bad Request

        verifyNoInteractions(commentService);
    }

    // =============== TESTS RECHERCHE PAR UTILISATEUR ===============

    @Test
    @DisplayName(" Lecture - Récupération des commentaires par utilisateur")
    void getCommentsByUserId_AuthenticatedUser_ShouldSucceed() throws Exception {
        // ARRANGE - Commentaires d'un utilisateur spécifique
        CommentModel userComment1 = new CommentModel("Mon premier commentaire");
        userComment1.setId(UUID.randomUUID());
        userComment1.setUser(legitimateUser);
        userComment1.setCreatedAt(LocalDateTime.now());
        userComment1.setUpdatedAt(LocalDateTime.now());

        CommentModel userComment2 = new CommentModel("Mon deuxième commentaire");
        userComment2.setId(UUID.randomUUID());
        userComment2.setUser(legitimateUser);
        userComment2.setCreatedAt(LocalDateTime.now());
        userComment2.setUpdatedAt(LocalDateTime.now());

        List<CommentModel> userComments = Arrays.asList(userComment1, userComment2);
        when(commentService.getByUserId(legitimateUserId)).thenReturn(userComments);

        // ACT & ASSERT - Lecture des commentaires d'un utilisateur
        mockMvc.perform(get("/api/comments/user/{userId}", legitimateUserId)
                        .with(user(maliciousUserDetails))) // N'importe qui peut lire (public)
                .andExpect(status().isOk()) //  Lecture OK
                .andExpect(jsonPath("$[0].content").value("Mon premier commentaire"))
                .andExpect(jsonPath("$[1].content").value("Mon deuxième commentaire"))
                .andExpect(jsonPath("$[0].userName").value("commentOwner"));

        verify(commentService).getByUserId(legitimateUserId);
    }

    @Test
    @DisplayName(" Erreur - Recherche commentaires pour utilisateur inexistant")
    void getCommentsByUserId_NonExistentUser_ShouldReturn404() throws Exception {
        // ARRANGE
        UUID nonExistentUserId = UUID.randomUUID();
        when(commentService.getByUserId(nonExistentUserId))
                .thenThrow(new UserNotFoundById(nonExistentUserId));

        // ACT & ASSERT
        mockMvc.perform(get("/api/comments/user/{userId}", nonExistentUserId)
                        .with(user(legitimateUserDetails)))
                .andExpect(status().isNotFound()) //  404 Not Found
                .andExpect(content().string("L'utilisateur avec l'ID " + nonExistentUserId + " n'existe pas"));

        verify(commentService).getByUserId(nonExistentUserId);
    }

    // =============== TESTS COHÉRENCE FINALE ===============

    @Test
    @DisplayName(" Test de cohérence globale - Isolation complète des commentaires")
    void commentSecurity_FullIsolation_ShouldMaintainIntegrity() throws Exception {
        // ARRANGE - Plusieurs commentaires de différents utilisateurs
        UUID comment1Id = UUID.randomUUID();
        UUID comment2Id = UUID.randomUUID();
        UUID comment3Id = UUID.randomUUID();

        // Configuration : seul le propriétaire peut modifier son commentaire
        when(commentService.updateComment(eq(comment1Id), any(CommentModel.class), eq(legitimateUserId)))
                .thenReturn(targetComment);
        when(commentService.updateComment(eq(comment2Id), any(CommentModel.class), eq(maliciousUserId)))
                .thenThrow(new UnauthorizedAccess());
        when(commentService.updateComment(eq(comment3Id), any(CommentModel.class), eq(legitimateUserId)))
                .thenThrow(new UnauthorizedAccess());

        // ACT & ASSERT - Utilisateur légitime peut modifier SON commentaire
        mockMvc.perform(put("/api/comments/{commentId}", comment1Id)
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()); //  Succès

        // ACT & ASSERT - Utilisateur malveillant ne peut PAS modifier le commentaire d'autrui
        mockMvc.perform(put("/api/comments/{commentId}", comment2Id)
                        .with(user(maliciousUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden()); //  Bloqué

        // ACT & ASSERT - Utilisateur légitime ne peut PAS modifier un autre commentaire
        mockMvc.perform(put("/api/comments/{commentId}", comment3Id)
                        .with(user(legitimateUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden()); //  Même lui est bloqué

        // Vérification de l'isolation complète
        verify(commentService).updateComment(eq(comment1Id), any(CommentModel.class), eq(legitimateUserId));
        verify(commentService).updateComment(eq(comment2Id), any(CommentModel.class), eq(maliciousUserId));
        verify(commentService).updateComment(eq(comment3Id), any(CommentModel.class), eq(legitimateUserId));
    }
}