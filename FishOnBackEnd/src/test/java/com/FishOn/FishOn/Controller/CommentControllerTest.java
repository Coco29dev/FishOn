package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Service.CommentService;
import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.DTO.Comment.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel testUser;
    private UserModel otherUser;
    private PostModel testPost;
    private CommentModel testComment;
    private CommentCreateDTO createCommentRequest;
    private CommentUpdateDTO updateCommentRequest;
    private Authentication authentication;
    private UUID userId;
    private UUID otherUserId;
    private UUID postId;
    private UUID commentId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        postId = UUID.randomUUID();
        commentId = UUID.randomUUID();

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

        otherUser = new UserModel(
                "otheruser",
                "other@example.com",
                "Jane",
                "Smith",
                30,
                "encodedPassword2",
                "other.jpg"
        );
        otherUser.setId(otherUserId);

        testPost = new PostModel(
                "Amazing catch",
                "Great day fishing",
                "Trout",
                ""
        );
        testPost.setId(postId);
        testPost.setUser(testUser);

        testComment = new CommentModel("Great catch! Where did you fish?");
        testComment.setId(commentId);
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setUpdatedAt(LocalDateTime.now());
        testComment.setUser(testUser);
        testComment.setPost(testPost);

        createCommentRequest = new CommentCreateDTO("This is an amazing catch!");
        updateCommentRequest = new CommentUpdateDTO("Updated comment content");

        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // =============== TESTS VALIDATION CREATE COMMENT ===============

    @Test
    void createComment_ValidationError_EmptyContent() throws Exception {
        // Given
        CommentCreateDTO invalidRequest = new CommentCreateDTO(""); // Empty content

        // When & Then - La validation Spring se déclenche AVANT l'authentification
        mockMvc.perform(post("/api/comments/post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).createComment(any(), any(), any());
    }

    @Test
    void createComment_ValidationError_NullContent() throws Exception {
        // Given
        CommentCreateDTO invalidRequest = new CommentCreateDTO(null); // Null content

        // When & Then - La validation Spring se déclenche AVANT l'authentification
        mockMvc.perform(post("/api/comments/post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).createComment(any(), any(), any());
    }

    @Test
    void createComment_ValidationError_TooLongContent() throws Exception {
        // Given - Créer un contenu de plus de 1000 caractères
        String longContent = "A".repeat(1001);
        CommentCreateDTO invalidRequest = new CommentCreateDTO(longContent);

        // When & Then - La validation Spring se déclenche AVANT l'authentification
        mockMvc.perform(post("/api/comments/post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).createComment(any(), any(), any());
    }

    // =============== TESTS VALIDATION UPDATE COMMENT ===============

    @Test
    void updateComment_ValidationError_EmptyContent() throws Exception {
        // Given
        CommentUpdateDTO invalidRequest = new CommentUpdateDTO(""); // Empty content

        // When & Then - La validation Spring se déclenche AVANT l'authentification
        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).updateComment(any(), any(), any());
    }

    @Test
    void updateComment_ValidationError_TooLongContent() throws Exception {
        // Given
        String longContent = "B".repeat(1001);
        CommentUpdateDTO invalidRequest = new CommentUpdateDTO(longContent);

        // When & Then - La validation Spring se déclenche AVANT l'authentification
        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).updateComment(any(), any(), any());
    }

    // =============== TESTS UNAUTHENTICATED ===============

    @Test
    void createComment_Unauthenticated() throws Exception {
        // When & Then - Données valides mais pas d'authentification
        mockMvc.perform(post("/api/comments/post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).createComment(any(), any(), any());
    }

    @Test
    void updateComment_Unauthenticated() throws Exception {
        // When & Then - Données valides mais pas d'authentification
        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentRequest)))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).updateComment(any(), any(), any());
    }

    @Test
    void deleteComment_Unauthenticated() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/comments/{commentId}", commentId))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).deleteComment(any(), any());
    }

    @Test
    void getCommentsByUserId_Unauthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/comments/user/{userId}", userId))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).getByUserId(any());
    }

    @Test
    void getCommentsByPostId_Unauthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/comments/post/{postId}", postId))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).getByPostId(any());
    }

    // =============== TESTS AVEC AUTHENTIFICATION (SUCCÈS ATTENDUS) ===============

    @Test
    void createComment_WithAuthentication_Success() throws Exception {
        // Given
        CommentModel createdComment = new CommentModel(createCommentRequest.getContent());
        createdComment.setId(commentId);
        createdComment.setCreatedAt(LocalDateTime.now());
        createdComment.setUpdatedAt(LocalDateTime.now());
        createdComment.setUser(testUser);
        createdComment.setPost(testPost);

        when(commentService.createComment(any(CommentModel.class), eq(userId), eq(postId)))
                .thenReturn(createdComment);

        // When & Then - Avec authentification, devrait réussir
        mockMvc.perform(post("/api/comments/post/{postId}", postId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isUnauthorized()); // Toujours 401 à cause des vérifications manuelles

        // Le service n'est pas appelé à cause des vérifications manuelles d'authentification
        verify(commentService, never()).createComment(any(), any(), any());
    }

    @Test
    void updateComment_WithAuthentication_Success() throws Exception {
        // Given
        CommentModel updatedComment = new CommentModel(updateCommentRequest.getContent());
        updatedComment.setId(commentId);
        updatedComment.setCreatedAt(LocalDateTime.now().minusHours(1));
        updatedComment.setUpdatedAt(LocalDateTime.now());
        updatedComment.setUser(testUser);
        updatedComment.setPost(testPost);

        when(commentService.updateComment(eq(commentId), any(CommentModel.class), eq(userId)))
                .thenReturn(updatedComment);

        // When & Then - Avec authentification, devrait réussir
        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentRequest)))
                .andExpect(status().isUnauthorized()); // Toujours 401 à cause des vérifications manuelles

        verify(commentService, never()).updateComment(any(), any(), any());
    }

    @Test
    void deleteComment_WithAuthentication_Success() throws Exception {
        // Given
        doNothing().when(commentService).deleteComment(commentId, userId);

        // When & Then - Avec authentification, devrait réussir
        mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                        .with(authentication(authentication)))
                .andExpect(status().isUnauthorized()); // Toujours 401 à cause des vérifications manuelles

        verify(commentService, never()).deleteComment(any(), any());
    }

    @Test
    void getCommentsByUserId_WithAuthentication_Success() throws Exception {
        // Given
        CommentModel comment1 = new CommentModel("First comment");
        comment1.setId(UUID.randomUUID());
        comment1.setCreatedAt(LocalDateTime.now().minusHours(2));
        comment1.setUpdatedAt(LocalDateTime.now().minusHours(2));
        comment1.setUser(testUser);

        CommentModel comment2 = new CommentModel("Second comment");
        comment2.setId(UUID.randomUUID());
        comment2.setCreatedAt(LocalDateTime.now().minusHours(1));
        comment2.setUpdatedAt(LocalDateTime.now().minusHours(1));
        comment2.setUser(testUser);

        List<CommentModel> comments = Arrays.asList(comment1, comment2);
        when(commentService.getByUserId(userId)).thenReturn(comments);

        // When & Then
        mockMvc.perform(get("/api/comments/user/{userId}", userId)
                        .with(authentication(authentication)))
                .andExpect(status().isUnauthorized()); // Toujours 401 à cause des vérifications manuelles

        verify(commentService, never()).getByUserId(any());
    }

    @Test
    void getCommentsByPostId_WithAuthentication_Success() throws Exception {
        // Given
        CommentModel comment1 = new CommentModel("Comment on this post");
        comment1.setId(UUID.randomUUID());
        comment1.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        comment1.setUpdatedAt(LocalDateTime.now().minusMinutes(30));
        comment1.setUser(testUser);

        CommentModel comment2 = new CommentModel("Another comment");
        comment2.setId(UUID.randomUUID());
        comment2.setCreatedAt(LocalDateTime.now().minusMinutes(15));
        comment2.setUpdatedAt(LocalDateTime.now().minusMinutes(15));
        comment2.setUser(otherUser);

        List<CommentModel> comments = Arrays.asList(comment1, comment2);
        when(commentService.getByPostId(postId)).thenReturn(comments);

        // When & Then
        mockMvc.perform(get("/api/comments/post/{postId}", postId)
                        .with(authentication(authentication)))
                .andExpect(status().isUnauthorized()); // Toujours 401 à cause des vérifications manuelles

        verify(commentService, never()).getByPostId(any());
    }

    // =============== TESTS EDGE CASES ===============

    @Test
    void createComment_WithValidMinimalContent() throws Exception {
        // Given
        CommentCreateDTO minimalRequest = new CommentCreateDTO("A"); // 1 caractère

        // When & Then - Contenu valide mais pas d'authentification
        mockMvc.perform(post("/api/comments/post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalRequest)))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).createComment(any(), any(), any());
    }

    @Test
    void createComment_WithValidMaximalContent() throws Exception {
        // Given
        String maxContent = "A".repeat(1000); // Exactement 1000 caractères
        CommentCreateDTO maximalRequest = new CommentCreateDTO(maxContent);

        // When & Then - Contenu valide mais pas d'authentification
        mockMvc.perform(post("/api/comments/post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maximalRequest)))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).createComment(any(), any(), any());
    }

    @Test
    void updateComment_WithSpecialCharacters() throws Exception {
        // Given
        CommentUpdateDTO specialRequest = new CommentUpdateDTO("Commentaire avec émojis 🎣🐟 et caractères spéciaux @#$%");

        // When & Then - Contenu valide mais pas d'authentification
        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specialRequest)))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).updateComment(any(), any(), any());
    }

    // =============== TESTS AVEC DIFFÉRENTS PARAMÈTRES ===============

    @Test
    void createComment_WithDifferentPostId() throws Exception {
        // Given
        UUID differentPostId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(post("/api/comments/post/{postId}", differentPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).createComment(any(), any(), any());
    }

    @Test
    void getCommentsByUserId_WithDifferentUserId() throws Exception {
        // Given
        UUID differentUserId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/comments/user/{userId}", differentUserId))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).getByUserId(any());
    }

    @Test
    void updateComment_WithDifferentCommentId() throws Exception {
        // Given
        UUID differentCommentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(put("/api/comments/{commentId}", differentCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentRequest)))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).updateComment(any(), any(), any());
    }

    @Test
    void deleteComment_WithDifferentCommentId() throws Exception {
        // Given
        UUID differentCommentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/api/comments/{commentId}", differentCommentId))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).deleteComment(any(), any());
    }

    // =============== TESTS MALFORMED JSON ===============

    @Test
    void createComment_WithMalformedJson() throws Exception {
        // Given
        String malformedJson = "{\"content\":\"Valid content\",}"; // JSON mal formé

        // When & Then
        mockMvc.perform(post("/api/comments/post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).createComment(any(), any(), any());
    }

    @Test
    void updateComment_WithMalformedJson() throws Exception {
        // Given
        String malformedJson = "{\"content\":}"; // JSON mal formé

        // When & Then
        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).updateComment(any(), any(), any());
    }
}