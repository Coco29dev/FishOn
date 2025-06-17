package com.FishOn.FishOn.Service;

import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    private UserModel testUser;
    private UserModel otherUser; // ✅ AJOUT : Un autre utilisateur pour les tests d'autorisation
    private PostModel testPost;
    private UUID userId;
    private UUID otherUserId; // ✅ AJOUT : ID d'un autre utilisateur
    private UUID postId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID(); // ✅ AJOUT : ID différent
        postId = UUID.randomUUID();

        testUser = new UserModel("testuser", "test@example.com", "John", "Doe", 25, "password", "profile.jpg");
        testUser.setId(userId);

        // ✅ AJOUT : Création d'un autre utilisateur avec un ID différent
        otherUser = new UserModel("otheruser", "other@example.com", "Jane", "Smith", 30, "password", "profile2.jpg");
        otherUser.setId(otherUserId);

        testPost = new PostModel("Superbe carpe", "Belle prise ce matin", "Carpe");
        testPost.setId(postId);
        testPost.setUser(testUser); // Le post appartient à testUser
    }

    @Test
    void validateData_Success() {
        // Given - testPost avec données valides

        // When & Then - ne doit pas lancer d'exception
        assertDoesNotThrow(() -> postService.validateData(testPost));
    }

    @Test
    void validateData_MissingTitle() {
        // Given
        testPost.setTitle(null);

        // When & Then
        assertThrows(MissingTitleException.class, () -> {
            postService.validateData(testPost);
        });
    }

    @Test
    void validateData_EmptyDescription() {
        // Given
        testPost.setDescription("   "); // Espaces seulement

        // When & Then
        assertThrows(MissingDescriptionException.class, () -> {
            postService.validateData(testPost);
        });
    }

    @Test
    void validateData_MissingFishName() {
        // Given
        testPost.setFishName("");

        // When & Then
        assertThrows(MissingFishNameException.class, () -> {
            postService.validateData(testPost);
        });
    }

    @Test
    void createPost_Success() throws Exception {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(PostModel.class))).thenReturn(testPost);

        // When
        PostModel result = postService.createPost(userId, testPost);

        // Then
        assertNotNull(result);
        assertEquals("Superbe carpe", result.getTitle());
        assertEquals(testUser, result.getUser());
        verify(postRepository).save(testPost);
    }

    @Test
    void createPost_UserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundById.class, () -> {
            postService.createPost(userId, testPost);
        });
    }

    @Test
    void updatePost_Success() throws Exception {
        // Given
        PostModel updatedPost = new PostModel("Carpe modifiée", "Description mise à jour", "Carpe commune");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(PostModel.class))).thenReturn(testPost);

        // When
        PostModel result = postService.updatePost(userId, postId, updatedPost);

        // Then
        assertNotNull(result);
        verify(postRepository).save(testPost);
    }

    @Test
    void updatePost_UnauthorizedUser() {
        // Given
        PostModel updatedPost = new PostModel("Titre", "Description", "Poisson");

        // ✅ CORRECTION : Utiliser otherUser avec un ID différent
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When & Then
        assertThrows(UnauthorizedModificationPost.class, () -> {
            postService.updatePost(otherUserId, postId, updatedPost); // ✅ CORRECTION : otherUserId
        });

        // Vérifier que save() n'a jamais été appelé
        verify(postRepository, never()).save(any());
    }

    @Test
    void deletePost_Success() throws Exception {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        postService.deletePost(userId, postId);

        // Then
        verify(postRepository).delete(testPost);
    }

    @Test
    void deletePost_UnauthorizedUser() {
        // Given
        // ✅ CORRECTION : Utiliser otherUser avec un ID différent
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When & Then
        assertThrows(UnauthorizedModificationPost.class, () -> {
            postService.deletePost(otherUserId, postId); // ✅ CORRECTION : otherUserId
        });

        // Vérifier que delete() n'a jamais été appelé
        verify(postRepository, never()).delete(any());
    }

    @Test
    void getAll_Success() {
        // Given
        List<PostModel> posts = Arrays.asList(testPost);
        when(postRepository.findAll()).thenReturn(posts);

        // When
        List<PostModel> result = postService.getAll();

        // Then
        assertEquals(1, result.size());
        assertEquals(testPost, result.get(0));
    }

    @Test
    void getByUserUserName_Success() throws Exception {
        // Given
        List<PostModel> posts = Arrays.asList(testPost);
        when(userRepository.existsByUserName("testuser")).thenReturn(true);
        when(postRepository.findByUserUserName("testuser")).thenReturn(posts);

        // When
        List<PostModel> result = postService.getByUserUserName("testuser");

        // Then
        assertEquals(1, result.size());
        assertEquals(testPost, result.get(0));
    }

    @Test
    void getByUserUserName_UserNotFound() {
        // Given
        when(userRepository.existsByUserName("nonexistent")).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundByUserName.class, () -> {
            postService.getByUserUserName("nonexistent");
        });
    }

    @Test
    void getByFishName_Success() throws Exception {
        // Given
        List<PostModel> posts = Arrays.asList(testPost);
        when(postRepository.existsByFishName("Carpe")).thenReturn(true);
        when(postRepository.findByFishName("Carpe")).thenReturn(posts);

        // When
        List<PostModel> result = postService.getByFishName("Carpe");

        // Then
        assertEquals(1, result.size());
        assertEquals("Carpe", result.get(0).getFishName());
    }

    @Test
    void getByFishName_NotFound() {
        // Given
        when(postRepository.existsByFishName("Requin")).thenReturn(false);

        // When & Then
        assertThrows(FishNameNotFound.class, () -> {
            postService.getByFishName("Requin");
        });
    }

    @Test
    void getByUserId_Success() throws Exception {
        // Given
        List<PostModel> posts = Arrays.asList(testPost);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(postRepository.findByUserId(userId)).thenReturn(posts);

        // When
        List<PostModel> result = postService.getByUserId(userId);

        // Then
        assertEquals(1, result.size());
        assertEquals(testPost, result.get(0));
    }

    @Test
    void getByUserId_UserNotFound() {
        // Given
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundById.class, () -> {
            postService.getByUserId(userId);
        });
    }

    @Test
    void getByLocation_Success() throws Exception {
        // Given
        testPost.setLocation("Lac de Annecy");
        List<PostModel> posts = Arrays.asList(testPost);
        when(postRepository.existsByLocation("Lac de Annecy")).thenReturn(true);
        when(postRepository.findByLocation("Lac de Annecy")).thenReturn(posts);

        // When
        List<PostModel> result = postService.getByLocation("Lac de Annecy");

        // Then
        assertEquals(1, result.size());
        assertEquals("Lac de Annecy", result.get(0).getLocation());
    }

    @Test
    void getByLocation_NotFound() {
        // Given
        when(postRepository.existsByLocation("Ocean Pacifique")).thenReturn(false);

        // When & Then
        assertThrows(LocationNotFound.class, () -> {
            postService.getByLocation("Ocean Pacifique");
        });
    }
}