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
    private UserModel otherUser;
    private PostModel testPost;
    private UUID userId;
    private UUID otherUserId;
    private UUID postId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        postId = UUID.randomUUID();

        testUser = new UserModel("testuser", "test@example.com", "John", "Doe", 25, "password", "profile.jpg");
        testUser.setId(userId);

        otherUser = new UserModel("otheruser", "other@example.com", "Jane", "Smith", 30, "password", "profile2.jpg");
        otherUser.setId(otherUserId);

        // ✅ CORRECTION : Utiliser le constructeur avec 4 paramètres (title, description, fishName, photoUrl)
        testPost = new PostModel("Superbe carpe", "Belle prise ce matin", "Carpe", "img/fish1.jpg");
        testPost.setId(postId);
        testPost.setUser(testUser);
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
    void validateData_MissingPhoto() {
        // Given
        testPost.setPhotoUrl(null);

        // When & Then
        assertThrows(MissingPhotoException.class, () -> {
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
    void createPost_MissingTitle() {
        // Given
        testPost.setTitle(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(MissingTitleException.class, () -> {
            postService.createPost(userId, testPost);
        });

        verify(postRepository, never()).save(any());
    }

    @Test
    void updatePost_Success() throws Exception {
        // Given
        // ✅ CORRECTION : Utiliser le constructeur avec 4 paramètres
        PostModel updatedPost = new PostModel("Carpe modifiée", "Description mise à jour", "Carpe commune", "img/fish2.jpg");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(PostModel.class))).thenReturn(testPost);

        // When
        PostModel result = postService.updatePost(userId, postId, updatedPost);

        // Then
        assertNotNull(result);
        verify(postRepository).save(testPost);
        // Vérifier que les champs ont été mis à jour
        assertEquals("Carpe modifiée", testPost.getTitle());
        assertEquals("Description mise à jour", testPost.getDescription());
        assertEquals("Carpe commune", testPost.getFishName());
        assertEquals("img/fish2.jpg", testPost.getPhotoUrl());
    }

    @Test
    void updatePost_UnauthorizedUser() {
        // Given
        // ✅ CORRECTION : Utiliser le constructeur avec 4 paramètres
        PostModel updatedPost = new PostModel("Titre", "Description", "Poisson", "img/fish3.jpg");

        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When & Then
        assertThrows(UnauthorizedModificationPost.class, () -> {
            postService.updatePost(otherUserId, postId, updatedPost);
        });

        verify(postRepository, never()).save(any());
    }

    @Test
    void updatePost_PostNotFound() {
        // Given
        PostModel updatedPost = new PostModel("Titre", "Description", "Poisson", "img/fish4.jpg");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PostNotFoundById.class, () -> {
            postService.updatePost(userId, postId, updatedPost);
        });

        verify(postRepository, never()).save(any());
    }

    @Test
    void updatePost_ValidationError() {
        // Given
        PostModel invalidPost = new PostModel("", "Description", "Poisson", "img/fish5.jpg"); // Titre vide
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When & Then
        assertThrows(MissingTitleException.class, () -> {
            postService.updatePost(userId, postId, invalidPost);
        });

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
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When & Then
        assertThrows(UnauthorizedModificationPost.class, () -> {
            postService.deletePost(otherUserId, postId);
        });

        verify(postRepository, never()).delete(any());
    }

    @Test
    void deletePost_PostNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PostNotFoundById.class, () -> {
            postService.deletePost(userId, postId);
        });

        verify(postRepository, never()).delete(any());
    }

    @Test
    void deletePost_UserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundById.class, () -> {
            postService.deletePost(userId, postId);
        });

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
    void getAll_EmptyList() {
        // Given
        when(postRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<PostModel> result = postService.getAll();

        // Then
        assertTrue(result.isEmpty());
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

    // =============== TESTS AVANCÉS ===============

    @Test
    void updatePost_WithOptionalFields() throws Exception {
        // Given
        PostModel updatedPost = new PostModel("Titre MAJ", "Description MAJ", "Brochet", "img/pike.jpg");
        updatedPost.setWeight(3.5);
        updatedPost.setLength(65.0);
        updatedPost.setLocation("Lac du Bourget");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(PostModel.class))).thenReturn(testPost);

        // When
        PostModel result = postService.updatePost(userId, postId, updatedPost);

        // Then
        assertNotNull(result);
        verify(postRepository).save(testPost);
        assertEquals(3.5, testPost.getWeight());
        assertEquals(65.0, testPost.getLength());
        assertEquals("Lac du Bourget", testPost.getLocation());
    }

    @Test
    void createPost_WithNullOptionalFields() throws Exception {
        // Given
        PostModel postWithNulls = new PostModel("Titre", "Description", "Truite", "img/trout.jpg");
        // Les champs optionnels restent null par défaut

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(PostModel.class))).thenReturn(postWithNulls);

        // When
        PostModel result = postService.createPost(userId, postWithNulls);

        // Then
        assertNotNull(result);
        assertNull(result.getWeight());
        assertNull(result.getLength());
        assertNull(result.getLocation());
        verify(postRepository).save(postWithNulls);
    }

    // =============== TESTS DES CONSTRUCTEURS ALTERNATIFS ===============

    @Test
    void createPost_WithConstructorNoArgs() throws Exception {
        // Given
        PostModel emptyPost = new PostModel(); // Constructeur vide
        emptyPost.setTitle("Titre via setter");
        emptyPost.setDescription("Description via setter");
        emptyPost.setFishName("Poisson via setter");
        emptyPost.setPhotoUrl("img/setter.jpg");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(PostModel.class))).thenReturn(emptyPost);

        // When
        PostModel result = postService.createPost(userId, emptyPost);

        // Then
        assertNotNull(result);
        assertEquals("Titre via setter", result.getTitle());
        verify(postRepository).save(emptyPost);
    }

    @Test
    void validateData_NullPost() {
        // Given
        PostModel nullPost = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            postService.validateData(nullPost);
        });
    }

    @Test
    void validateData_AllNullFields() {
        // Given
        PostModel nullFieldsPost = new PostModel();
        // Tous les champs restent null

        // When & Then
        assertThrows(MissingTitleException.class, () -> {
            postService.validateData(nullFieldsPost);
        });
    }
}