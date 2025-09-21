package com.FishOn.FishOn.Service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.FishOn.FishOn.Exception.FishOnException.CommentNotFound;
import com.FishOn.FishOn.Exception.FishOnException.PostNotFoundById;
import com.FishOn.FishOn.Exception.FishOnException.UnauthorizedAccess;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundById;
import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.CommentRepository;
import com.FishOn.FishOn.Repository.PostRepository;
import com.FishOn.FishOn.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    // ========== CONSTANTE ==========
    private static final UUID VALID_USER_ID = UUID.randomUUID();
    private static final UUID INVALID_USER_ID = UUID.randomUUID();
    private static final UUID INVALID_POST_ID = UUID.randomUUID();
    private static final UUID INVALID_COMMENT_ID = UUID.randomUUID();

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentService commentService;

    // ========== FONCTION HELPER ==========
    private CommentModel createdComment() {
        return new CommentModel(
                "Commentaires d'un utilisateur");
    }

    private CommentModel updatedComment() {
        return new CommentModel(
            "Commentaires MAJ"
        );
    }
    
    private UserModel createUser() {
        return new UserModel(
                "user1",
                "user1@fishon.com",
                "J",
                "D",
                25,
                "userpassword",
                "");
    }

    private PostModel createPost() {
        return new PostModel(
                "title",
                "Magnifique prise",
                "Carpe",
                "fishPicture");
    }

    private void assertDeleteCommentThrowsException(
        UUID commentId,
        UUID userId,
        Class <? extends Exception> expectedExceptionType,
        String expectedMessage) {
        
        assertThatThrownBy(() -> commentService.deleteComment(commentId, userId))
            .isInstanceOf(expectedExceptionType)
            .hasMessage(expectedMessage);
    }

    // ========== TEST MÉTHODE CRUD ==========

    @Test
    @DisplayName("Création commentaire - valide")
    void createComment() throws UserNotFoundById, PostNotFoundById {
        // ARRANGE - Préparation des données
        UserModel user = createUser();
        UUID userId = user.getId();
        PostModel post = createPost();
        UUID postId = post.getId();
        CommentModel comment = createdComment();

        // Configuration mock
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(CommentModel.class))).thenReturn(comment);

        // ACT
        CommentModel result = commentService.createComment(comment, userId, postId);

        // ASSERT - vérification des données
        assertNotNull(result);
        assertEquals(comment, result);
        assertEquals(user, result.getUser());
        assertEquals(post, result.getPost());

        // Vérification interractions
        verify(userRepository).findById(userId);
        verify(postRepository).findById(postId);
        verify(commentRepository).save(any(CommentModel.class));
    }

    @Test
    @DisplayName("Création commentaire - UserNotFoundById")
    void createdCommentUserNotFound() throws UserNotFoundById {
        // ARRANGE - Préparation des données
        PostModel post = createPost();
        UUID postId = post.getId();
        CommentModel comment = createdComment();

        // Configuration mocks
        when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> commentService.createComment(comment, INVALID_USER_ID, postId))
                .isInstanceOf(UserNotFoundById.class)
                .hasMessage("L'utilisateur avec l'ID " + INVALID_USER_ID + " n'existe pas");

        verify(userRepository).findById(INVALID_USER_ID);
        verifyNoInteractions(postRepository);
        verify(commentRepository, never()).save(any(CommentModel.class));
    }
    
    @Test
    @DisplayName("Création commentaire - PostNotFoundById")
    void createdCommentPostNotFound() throws PostNotFoundById {
        // ARRANGE - Préparation des données
        UserModel user = createUser();
        UUID userId = user.getId();
        CommentModel comment = createdComment();

        // Configuration mock
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(INVALID_POST_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> commentService.createComment(comment, userId, INVALID_POST_ID))
            .isInstanceOf(PostNotFoundById.class)
            .hasMessage("La publication n'existe pas");
            
        verify(userRepository).findById(userId);
        verify(postRepository).findById(INVALID_POST_ID);
        verify(commentRepository, never()).save(any(CommentModel.class));
    }

    @Test
    @DisplayName("MAJ commentaire - valide")
    void updateComment() throws CommentNotFound, UnauthorizedAccess {
        // ARRANGE - Préparation des données
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserModel user = createUser();
        user.setId(userId); // ← Important : définir l'ID

        CommentModel existingComment = createdComment();
        existingComment.setId(commentId);
        existingComment.setUser(user); // ← Le commentaire appartient à user
        existingComment.setContent("Ancien contenu");

        CommentModel updatedData = updatedComment(); // Nouvelles données

        CommentModel savedComment = createdComment();
        savedComment.setContent(updatedData.getContent());
        savedComment.setUser(user);

        // Configuration mocks
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(existingComment)).thenReturn(savedComment);

        // ACT
        CommentModel result = commentService.updateComment(commentId, updatedData, userId);

        // ASSERT - Vérification des données
        assertNotNull(result);
        assertEquals(updatedData.getContent(), existingComment.getContent()); // Contenu mis à jour

        // Vérification interactions
        verify(commentRepository).findById(commentId);
        verify(commentRepository).save(existingComment);
    }
    
    @Test
    @DisplayName("MAJ commentaire - CommentNotFound")
    void updateCommentNotFound() {
        // ARRANGE - Préparation des données
        UUID nonExistentCommentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CommentModel updatedData = updatedComment();

        // Configuration mock
        when(commentRepository.findById(nonExistentCommentId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> commentService.updateComment(nonExistentCommentId, updatedData, userId))
                .isInstanceOf(CommentNotFound.class)
                .hasMessage("Le commentaire " + nonExistentCommentId + " n'existe pas");

        verify(commentRepository).findById(nonExistentCommentId);
        verify(commentRepository, never()).save(any(CommentModel.class));
    }
    
    @Test
    @DisplayName("MAJ commentaire - UnauthorizedAccess")
    void updateCommentUnauthorized() {
        // ARRANGE - Préparation des données
        UUID commentId = UUID.randomUUID();
        UUID commentOwnerId = UUID.randomUUID(); // Vrai propriétaire
        UUID attemptingUserId = UUID.randomUUID(); // Utilisateur qui tente

        UserModel commentOwner = createUser();
        commentOwner.setId(commentOwnerId);

        CommentModel existingComment = createdComment();
        existingComment.setId(commentId);
        existingComment.setUser(commentOwner); // ← Appartient à commentOwner

        CommentModel updatedData = updatedComment();

        // Configuration mock
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> commentService.updateComment(commentId, updatedData, attemptingUserId))
                .isInstanceOf(UnauthorizedAccess.class)
                .hasMessage("N'est pas autorisé à modifier");

        verify(commentRepository).findById(commentId);
        verify(commentRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Suppression commentaire - valide")
    void deleteComment() throws CommentNotFound, UnauthorizedAccess {
        // ARRANGE - Préparation des données
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserModel user = createUser();
        user.setId(userId);

        CommentModel existingComment = createdComment();
        existingComment.setId(commentId);
        existingComment.setUser(user); // ← Le commentaire appartient à user

        // Configuration mock
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // ACT
        commentService.deleteComment(commentId, userId);

        // ASSERT - Vérification interactions
        verify(commentRepository).findById(commentId);
        verify(commentRepository).delete(existingComment);
    }

    @Test
    @DisplayName("Suppression commentaires - CommentNotFound")
    void deleteCommentNotFound() throws CommentNotFound {
        // ARRANGE - Configuration mock
        when(commentRepository.findById(INVALID_COMMENT_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT - Lancement exception
        assertDeleteCommentThrowsException(INVALID_COMMENT_ID, VALID_USER_ID,
                CommentNotFound.class,
                "Le commentaire " + INVALID_COMMENT_ID + " n'existe pas");

        // Vérification interractions
        verify(commentRepository).findById(INVALID_COMMENT_ID);
    }
    
    @Test
    @DisplayName("Suppression commentaire - UnauthorizedAccess")
    void deleteCommentUnauthorized() {
        // ARRANGE - Préparation des données
        UUID commentId = UUID.randomUUID();
        UUID commentOwnerId = UUID.randomUUID();
        UUID attemptingUserId = UUID.randomUUID();

        UserModel commentOwner = createUser();
        commentOwner.setId(commentOwnerId);

        CommentModel existingComment = createdComment();
        existingComment.setId(commentId);
        existingComment.setUser(commentOwner);

        // Configuration mock
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // ACT & ASSERT - Lancement exception
        assertDeleteCommentThrowsException(commentId, attemptingUserId,
                UnauthorizedAccess.class,
                "N'est pas autorisé à modifier");

        // Vérification interractions
        verify(commentRepository).findById(commentId);
        verify(commentRepository, never()).delete(any());
    }
    
    // ========== TESTS MÉTHODES REPOSITORY ==========

    @Test
    @DisplayName("Récupération commentaires par userId - valide")
    void getCommentsByUserId() throws UserNotFoundById {
        // ARRANGE - Préparation des données
        UUID userId = UUID.randomUUID();
        CommentModel comment1 = createdComment();
        CommentModel comment2 = createdComment();
        List<CommentModel> expectedComments = Arrays.asList(comment1, comment2);

        // Configuration mocks
        when(userRepository.existsById(userId)).thenReturn(true);
        when(commentRepository.findByUserId(userId)).thenReturn(expectedComments);

        // ACT
        List<CommentModel> result = commentService.getByUserId(userId);

        // ASSERT - Vérification des données
        assertNotNull(result);
        assertEquals(expectedComments, result);
        assertEquals(2, result.size());

        // Vérification interactions
        verify(userRepository).existsById(userId);
        verify(commentRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Récupération commentaires par userId - UserNotFoundById")
    void getCommentsByUserIdNotFound() {
        // ARRANGE - Configuration mock
        when(userRepository.existsById(INVALID_USER_ID)).thenReturn(false);

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> commentService.getByUserId(INVALID_USER_ID))
            .isInstanceOf(UserNotFoundById.class)
            .hasMessage("L'utilisateur avec l'ID " + INVALID_USER_ID + " n'existe pas");

        // Vérification interactions
        verify(userRepository).existsById(INVALID_USER_ID);
        verify(commentRepository, never()).findByUserId(any());
    }

    @Test
    @DisplayName("Récupération commentaires par postId - valide")
    void getCommentsByPostId() throws PostNotFoundById {
        // ARRANGE - Préparation des données
        UUID postId = UUID.randomUUID();
        CommentModel comment1 = createdComment();
        CommentModel comment2 = createdComment();
        List<CommentModel> expectedComments = Arrays.asList(comment1, comment2);

        // Configuration mocks
        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findByPostId(postId)).thenReturn(expectedComments);

        // ACT
        List<CommentModel> result = commentService.getByPostId(postId);

        // ASSERT - Vérification des données
        assertNotNull(result);
        assertEquals(expectedComments, result);

        // Vérification interactions
        verify(postRepository).existsById(postId);
        verify(commentRepository).findByPostId(postId);
    }

    @Test
    @DisplayName("Récupération commentaires par postId - PostNotFoundById")
    void getCommentsByPostIdNotFound() {
        // ARRANGE - Configuration mock
        when(postRepository.existsById(INVALID_POST_ID)).thenReturn(false);

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> commentService.getByPostId(INVALID_POST_ID))
            .isInstanceOf(PostNotFoundById.class)
            .hasMessage("La publication n'existe pas");

        // Vérification interactions
        verify(postRepository).existsById(INVALID_POST_ID);
        verify(commentRepository, never()).findByPostId(any());
    }
}