package com.FishOn.FishOn.Service;

import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.CommentRepository;
import com.FishOn.FishOn.Repository.PostRepository;
import com.FishOn.FishOn.Repository.UserRepository;
import com.FishOn.FishOn.Exception.FishOnException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    private UserModel testUser;
    private UserModel otherUser;
    private PostModel testPost;
    private CommentModel testComment;
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

        // Création utilisateur principal
        testUser = new UserModel("testuser", "test@example.com", "John", "Doe", 25, "password", "profile.jpg");
        testUser.setId(userId);

        // Création autre utilisateur pour tests d'autorisation
        otherUser = new UserModel("otheruser", "other@example.com", "Jane", "Smith", 30, "password", "profile2.jpg");
        otherUser.setId(otherUserId);

        // ✅ CORRECTION : Utiliser le constructeur PostModel avec 4 paramètres (title, description, fishName, photoUrl)
        testPost = new PostModel("Superbe carpe", "Belle prise ce matin", "Carpe", "img/carpe.jpg");
        testPost.setId(postId);
        testPost.setUser(testUser);

        // Création commentaire de test
        testComment = new CommentModel("Très belle prise, félicitations !");
        testComment.setId(commentId);
        testComment.setUser(testUser);
        testComment.setPost(testPost);
    }

    // =============== TESTS CRUD ===============

    @Test
    void createComment_Success() throws Exception {
        // Given
        CommentModel newComment = new CommentModel("Super commentaire !");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(CommentModel.class))).thenReturn(testComment);

        // When
        CommentModel result = commentService.createComment(newComment, userId, postId);

        // Then
        assertNotNull(result);
        assertEquals(testComment.getContent(), result.getContent());
        assertEquals(testUser, result.getUser());
        assertEquals(testPost, result.getPost());

        // Vérification que les associations ont été définies
        verify(commentRepository).save(newComment);
        assertEquals(testUser, newComment.getUser());
        assertEquals(testPost, newComment.getPost());
    }

    @Test
    void createComment_UserNotFound() {
        // Given
        CommentModel newComment = new CommentModel("Commentaire test");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundById.class, () -> {
            commentService.createComment(newComment, userId, postId);
        });

        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_PostNotFound() {
        // Given
        CommentModel newComment = new CommentModel("Commentaire test");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PostNotFoundById.class, () -> {
            commentService.createComment(newComment, userId, postId);
        });

        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_Success() throws Exception {
        // Given
        CommentModel updatedComment = new CommentModel("Commentaire modifié");
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(CommentModel.class))).thenReturn(testComment);

        // When
        CommentModel result = commentService.updateComment(commentId, updatedComment, userId);

        // Then
        assertNotNull(result);
        verify(commentRepository).save(testComment);
        // Vérifier que le contenu a été mis à jour
        assertEquals("Commentaire modifié", testComment.getContent());
    }

    @Test
    void updateComment_CommentNotFound() {
        // Given
        CommentModel updatedComment = new CommentModel("Commentaire modifié");
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CommentNotFound.class, () -> {
            commentService.updateComment(commentId, updatedComment, userId);
        });

        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_UnauthorizedAccess() {
        // Given
        CommentModel updatedComment = new CommentModel("Commentaire modifié");
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));

        // When & Then - otherUserId essaie de modifier le commentaire de testUser
        assertThrows(UnauthorizedAccess.class, () -> {
            commentService.updateComment(commentId, updatedComment, otherUserId);
        });

        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_Success() throws Exception {
        // Given
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));

        // When
        commentService.deleteComment(commentId, userId);

        // Then
        verify(commentRepository).delete(testComment);
    }

    @Test
    void deleteComment_CommentNotFound() {
        // Given
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CommentNotFound.class, () -> {
            commentService.deleteComment(commentId, userId);
        });

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_UnauthorizedAccess() {
        // Given
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));

        // When & Then - otherUserId essaie de supprimer le commentaire de testUser
        assertThrows(UnauthorizedAccess.class, () -> {
            commentService.deleteComment(commentId, otherUserId);
        });

        verify(commentRepository, never()).delete(any());
    }

    // =============== TESTS DE RECHERCHE ===============

    @Test
    void getByUserId_Success() throws Exception {
        // Given
        CommentModel comment2 = new CommentModel("Autre commentaire");
        comment2.setUser(testUser);
        List<CommentModel> comments = Arrays.asList(testComment, comment2);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(commentRepository.findByUserId(userId)).thenReturn(comments);

        // When
        List<CommentModel> result = commentService.getByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testComment, result.get(0));
        assertEquals(comment2, result.get(1));
    }

    @Test
    void getByUserId_UserNotFound() {
        // Given
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundById.class, () -> {
            commentService.getByUserId(userId);
        });

        verify(commentRepository, never()).findByUserId(any());
    }

    @Test
    void getByUserId_EmptyList() throws Exception {
        // Given
        when(userRepository.existsById(userId)).thenReturn(true);
        when(commentRepository.findByUserId(userId)).thenReturn(Arrays.asList());

        // When
        List<CommentModel> result = commentService.getByUserId(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByPostId_Success() throws Exception {
        // Given
        CommentModel comment2 = new CommentModel("Deuxième commentaire");
        comment2.setUser(otherUser);
        comment2.setPost(testPost);
        List<CommentModel> comments = Arrays.asList(testComment, comment2);

        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findByPostId(postId)).thenReturn(comments);

        // When
        List<CommentModel> result = commentService.getByPostId(postId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testComment, result.get(0));
        assertEquals(comment2, result.get(1));
    }

    @Test
    void getByPostId_PostNotFound() {
        // Given
        when(postRepository.existsById(postId)).thenReturn(false);

        // When & Then
        assertThrows(PostNotFoundById.class, () -> {
            commentService.getByPostId(postId);
        });

        verify(commentRepository, never()).findByPostId(any());
    }

    @Test
    void getByPostId_EmptyList() throws Exception {
        // Given
        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findByPostId(postId)).thenReturn(Arrays.asList());

        // When
        List<CommentModel> result = commentService.getByPostId(postId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =============== TESTS DE CAS LIMITES ===============

    @Test
    void createComment_WithNullContent() throws Exception {
        // Given
        CommentModel newComment = new CommentModel(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(CommentModel.class))).thenReturn(newComment);

        // When
        CommentModel result = commentService.createComment(newComment, userId, postId);

        // Then
        assertNotNull(result);
        verify(commentRepository).save(newComment);
    }

    @Test
    void updateComment_WithEmptyContent() throws Exception {
        // Given
        CommentModel updatedComment = new CommentModel("");
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(CommentModel.class))).thenReturn(testComment);

        // When
        CommentModel result = commentService.updateComment(commentId, updatedComment, userId);

        // Then
        assertNotNull(result);
        assertEquals("", testComment.getContent());
        verify(commentRepository).save(testComment);
    }

    @Test
    void createComment_WithLongContent() throws Exception {
        // Given
        String longContent = "a".repeat(1000); // 1000 caractères
        CommentModel newComment = new CommentModel(longContent);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(CommentModel.class))).thenReturn(newComment);

        // When
        CommentModel result = commentService.createComment(newComment, userId, postId);

        // Then
        assertNotNull(result);
        assertEquals(longContent, result.getContent());
        verify(commentRepository).save(newComment);
    }

    // =============== TESTS AVANCÉS ===============

    @Test
    void createComment_WithSpecialCharacters() throws Exception {
        // Given
        String specialContent = "Commentaire avec émojis 🎣🐟 et caractères spéciaux äöüß@#$%^&*()";
        CommentModel newComment = new CommentModel(specialContent);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(CommentModel.class))).thenReturn(newComment);

        // When
        CommentModel result = commentService.createComment(newComment, userId, postId);

        // Then
        assertNotNull(result);
        assertEquals(specialContent, result.getContent());
        verify(commentRepository).save(newComment);
    }

    @Test
    void createComment_MultipleCommentsOnSamePost() throws Exception {
        // Given
        CommentModel comment1 = new CommentModel("Premier commentaire");
        CommentModel comment2 = new CommentModel("Deuxième commentaire");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(CommentModel.class)))
                .thenReturn(comment1)
                .thenReturn(comment2);

        // When
        CommentModel result1 = commentService.createComment(comment1, userId, postId);
        CommentModel result2 = commentService.createComment(comment2, userId, postId);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1.getContent(), result2.getContent());
        verify(commentRepository, times(2)).save(any(CommentModel.class));
    }

    @Test
    void updateComment_SameContentMultipleTimes() throws Exception {
        // Given
        CommentModel updatedComment = new CommentModel("Contenu identique");
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(CommentModel.class))).thenReturn(testComment);

        // When
        CommentModel result1 = commentService.updateComment(commentId, updatedComment, userId);
        CommentModel result2 = commentService.updateComment(commentId, updatedComment, userId);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("Contenu identique", testComment.getContent());
        verify(commentRepository, times(2)).save(testComment);
    }

    @Test
    void createComment_WithMaxContentLength() throws Exception {
        // Given - Test avec contenu très long (limite probable de la DB)
        String maxContent = "a".repeat(5000); // 5000 caractères
        CommentModel newComment = new CommentModel(maxContent);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(CommentModel.class))).thenReturn(newComment);

        // When
        CommentModel result = commentService.createComment(newComment, userId, postId);

        // Then
        assertNotNull(result);
        assertEquals(maxContent, result.getContent());
        assertEquals(5000, result.getContent().length());
        verify(commentRepository).save(newComment);
    }

    @Test
    void getByUserId_WithMultipleUsers() throws Exception {
        // Given
        CommentModel userComment = new CommentModel("Commentaire de testUser");
        userComment.setUser(testUser);

        CommentModel otherComment = new CommentModel("Commentaire de otherUser");
        otherComment.setUser(otherUser);

        List<CommentModel> userComments = Arrays.asList(userComment);
        List<CommentModel> otherComments = Arrays.asList(otherComment);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.existsById(otherUserId)).thenReturn(true);
        when(commentRepository.findByUserId(userId)).thenReturn(userComments);
        when(commentRepository.findByUserId(otherUserId)).thenReturn(otherComments);

        // When
        List<CommentModel> userResult = commentService.getByUserId(userId);
        List<CommentModel> otherResult = commentService.getByUserId(otherUserId);

        // Then
        assertEquals(1, userResult.size());
        assertEquals(1, otherResult.size());
        assertEquals("Commentaire de testUser", userResult.get(0).getContent());
        assertEquals("Commentaire de otherUser", otherResult.get(0).getContent());
        assertNotEquals(userResult.get(0).getUser().getId(), otherResult.get(0).getUser().getId());
    }
}