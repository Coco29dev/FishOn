package com.FishOn.FishOn.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import com.FishOn.FishOn.Exception.FishOnException.MissingTitleException;
import com.FishOn.FishOn.Exception.FishOnException.PostNotFoundById;
import com.FishOn.FishOn.Exception.FishOnException.UnauthorizedModificationPost;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundById;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundByUserName;
import com.FishOn.FishOn.Exception.FishOnException.FishNameNotFound;
import com.FishOn.FishOn.Exception.FishOnException.LocationNotFound;
import com.FishOn.FishOn.Exception.FishOnException.MissingDescriptionException;
import com.FishOn.FishOn.Exception.FishOnException.MissingFishNameException;
import com.FishOn.FishOn.Exception.FishOnException.MissingPhotoException;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.PostRepository;
import com.FishOn.FishOn.Repository.UserRepository;
import com.github.dockerjava.api.exception.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    // Constantes
    private static final UUID VALID_USER_ID = UUID.randomUUID();
    private static final UUID INVALID_USER_ID = UUID.randomUUID();
    private static final UUID VALID_POST_ID = UUID.randomUUID();
    private static final UUID INVALID_POST_ID = UUID.randomUUID();

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PostModel postModel;

    @InjectMocks
    PostService postService;

    // ========== FONCTION HELPER ==========

    private PostModel createPost() {
        return new PostModel(
                "title",
                "Magnifique prise",
                "Carpe",
                "fishPicture");
    }

    private PostModel updatedPost() {
        return new PostModel(
                "title1",
                "Magnifique prise1",
                "Carpe1",
                "fishPicture1");
    }

    private List<PostModel> createListPosts(PostModel post1, PostModel post2) {
        List<PostModel> posts = new ArrayList<>();
        posts.add(post1);
        posts.add(post2);
        return posts;
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

    // ========== MÉTHODE HELPER ==========
    private void assertValidationThrowsException(
            String title,
            String description,
            String fishName,
            String photoUrl,
            // Class équivalent Exception.class
            // <?> = représente n'importe quelle classe
            Class<? extends Exception> expectedExceptionType,
            String expectedMessage) {
        //ARRANGE
        PostModel post = new PostModel(title, description, fishName, photoUrl);

        //ACT & ASSERT
        assertThatThrownBy(() -> postService.validateData(post))
                .isInstanceOf(expectedExceptionType)
                .hasMessage(expectedMessage);
    }

    private void assertUpdatePostThrowsException(
        UUID userId, UUID postId, PostModel updatedPost,
        Class <? extends Exception> expectedExceptionType,
        String expectedMessage) {
        // ACT & ASSERT
        assertThatThrownBy(() -> postService.updatePost(userId, postId, updatedPost))
                .isInstanceOf(expectedExceptionType)
                .hasMessage(expectedMessage);
    }

    private void assertDeletePostThrowsException(UUID userId, UUID postId,
        Class <? extends Exception> expectedExceptionType, String expectedMessage) {
        // ACT & ASSERT
        assertThatThrownBy(() -> postService.deletePost(userId, postId))
        .isInstanceOf(expectedExceptionType)
        .hasMessage(expectedMessage);
    }

    // ========== TEST MÉTHODE VALIDATION ==========

    @Test
    @DisplayName("Données valide")
    void validData() {
        // ARRANGE
        PostModel post = createPost();

        // ACT & ASSERT
        assertDoesNotThrow(() -> postService.validateData(post));
    }

    @Test
    @DisplayName("Données invalide - Titre null")
    void nullTitle() {
        // Appel fonction helper
        assertValidationThrowsException(
            null,
            "Magnifique prise",
            "Carpe",
            "fishPicture",
            MissingTitleException.class,
            "Titre obligatoire");
    }

    @Test
    @DisplayName("Données invalide - Titre vide")
    void emptyTitle() {
        // Appel fonction helper
        assertValidationThrowsException(
                "",
                "Magnifique prise",
                "Carpe",
                "fishPicture",
                MissingTitleException.class,
                "Titre obligatoire");
    }
    
    @Test
    @DisplayName("Données invalide - Description null")
    void nullDescription() {
        // Appel fonction helper
        assertValidationThrowsException(
                "title",
                null,
                "Carpe",
                "fishPicture",
                MissingDescriptionException.class,
                "Description obligatoire");
    }

    @Test
    @DisplayName("Données invalide - Description vide")
    void emptyDescription() {
        // Appel fonction helper
        assertValidationThrowsException(
                "title",
                "",
                "Carpe",
                "fishPicture",
                MissingDescriptionException.class,
                "Description obligatoire");
    }

    @Test
    @DisplayName("Données invalide - fishName null")
    void nullFishName() {
        // Appel fonction helper
        assertValidationThrowsException(
                "title",
                "Magnifique prise",
                null,
                "fishPicture",
                MissingFishNameException.class,
                "fishName obligatoire");
    }

    @Test
    @DisplayName("Données invalide - fishName vide")
    void emptyFishName() {
        // Appel fonction helper
        assertValidationThrowsException(
                "title",
                "Magnifique prise",
                "",
                "fishPicture",
                MissingFishNameException.class,
                "fishName obligatoire");
    }

    @Test
    @DisplayName("Données invalide - photoUrl null")
    void nullPhotoUrl() {
        // Appel fonction helper
        assertValidationThrowsException(
                "title",
                "Magnifique prise",
                "Carpe",
                null,
                MissingPhotoException.class,
                "Photo obligatoire");
    }

    @Test
    @DisplayName("Données invalide - photoUrl vide")
    void emptyPhotoUrl() {
        // Appel fonction helper
        assertValidationThrowsException(
                "title",
                "Magnifique prise",
                "Carpe",
                "",
                MissingPhotoException.class,
                "Photo obligatoire");
    }

    // ========== TEST MÉTHODE CRUD ==========

    // ========== TEST CRÉATION PUBLICATION ==========

    @Test
    @DisplayName("Création publication - valide")
    void createPostValidDataSuccess() throws UserNotFoundById, MissingTitleException,
            MissingDescriptionException, MissingFishNameException, MissingPhotoException {
        // ARRANGE - Préparation des données
        PostModel validPost = createPost();
        PostModel savedPost = new PostModel( // ← Output du repository mock
                "title", "description", "fishName", "photo");
        UserModel mockUser = new UserModel(
                "testUser", "test@email.com", "Test", "User", 25, "password", "profile.jpg");

        // Configuration mocks
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(mockUser));
        when(postRepository.save(any(PostModel.class))).thenReturn(savedPost);

        // ACT
        PostModel result = postService.createPost(VALID_USER_ID, validPost);

        // ASSERT - Vérification
        assertNotNull(result);
        assertEquals(savedPost, result);

        // Vérifications interractions méthodes
        verify(userRepository).findById(VALID_USER_ID);
        verify(postRepository).save(validPost);
    }
    
    @Test
    @DisplayName("Création Publication - UserNotFounById")
    void creatPostUserNotFound() throws UserNotFoundById {
        // ARRANGE - Préparation des données
        PostModel validPost = createPost();

        // Configuration mock
        when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT - Lancement excpetion
        assertThatThrownBy(() -> postService.createPost(INVALID_USER_ID, validPost))
                .isInstanceOf(UserNotFoundById.class)
                .hasMessage("L'utilisateur avec l'ID " + INVALID_USER_ID + " n'existe pas");
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    @DisplayName("MAJ Publication - valide")
    void updtaedPostValid() throws UserNotFoundById, PostNotFoundById, UnauthorizedModificationPost, MissingTitleException,
            MissingDescriptionException, MissingFishNameException, MissingPhotoException {
        // ARRANGE - Préparation de données
        UserModel user = createUser();
        PostModel post = createPost();
        PostModel updatePost = updatedPost();

        // Configuration Setter
        user.setId(VALID_USER_ID);
        post.setUser(user);
        post.setId(VALID_POST_ID);

        // Configuration mock
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        when(postRepository.findById(VALID_POST_ID)).thenReturn(Optional.of(post));
        when(postRepository.save(any(PostModel.class))).thenReturn(updatePost);

        // ACT - Appel méthode
        PostModel result = postService.updatePost(VALID_USER_ID, VALID_POST_ID, updatePost);

        // ASSERT - vérification des données
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo(updatePost.getTitle());
        assertThat(result.getDescription()).isEqualTo(updatePost.getDescription());
        assertThat(result.getFishName()).isEqualTo(updatePost.getFishName());
        assertThat(result.getPhotoUrl()).isEqualTo(updatePost.getPhotoUrl());

        // Vérification interractions
        verify(userRepository).findById(VALID_USER_ID);
        verify(postRepository).findById(VALID_POST_ID);
        verify(postRepository).save(any(PostModel.class));
    }

    @Test
    @DisplayName("MAJ Publication - UserNotFoundById")
    void updatedPostUserNotFound() {
        // ARRANGE
        PostModel updatedPost = createPost();

        // Configuration mock
        when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT - utilisation fonction helper
        assertUpdatePostThrowsException(INVALID_USER_ID, VALID_POST_ID, updatedPost,
                UserNotFoundById.class,
                "L'utilisateur avec l'ID " + INVALID_USER_ID + " n'existe pas");
    }

    @Test
    @DisplayName("MAJ Publication - PostNotFoundById")
    void updatedPostNotFound() {
        // ARRANGE
        PostModel updatedPost = createPost();
        UserModel user = createUser();

        // Configuration mock
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        when(postRepository.findById(INVALID_POST_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT - utilisation fonction helper
        assertUpdatePostThrowsException(VALID_USER_ID, INVALID_POST_ID, updatedPost,
                PostNotFoundById.class,
                "La publication n'existe pas");
    }

    @Test
    @DisplayName("MAJ Publication - UnauthorizedModificationPost")
    void updatedPostUnauthorizedModification() {
        // ARRANGE - Préparation des données
        UserModel user = createUser(); // Utilisateur qui tente la création
        UserModel owner = createUser(); // Propriétaire réel du post
        PostModel post = createPost(); // Post existant
        PostModel updatedPost = updatedPost(); // Nouvelles données
        UUID ownerId = UUID.randomUUID(); // ID propriétaire réel du post
        UUID postId = UUID.randomUUID(); // ID post à modifier

        // Configuraion Setter
        user.setId(VALID_USER_ID);
        owner.setId(ownerId);
        post.setUser(owner);

        // Configuration mock
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // ACT & ASSERT
        assertThatThrownBy(() -> postService.updatePost(user.getId(), postId, updatedPost))
                .isInstanceOf(UnauthorizedModificationPost.class)
                .hasMessage("N'est pas autorisé à modifier cette publication");

        // Vérification interractions
        verify(userRepository).findById(user.getId());
        verify(postRepository).findById(postId);
        verify(postRepository, never()).save(any(PostModel.class));
    }
    
    // ========== TEST SUPPRESSION PUBLICATION ==========

    @Test
    @DisplayName("Suppression publication - valide")
    void deletedPost() throws UserNotFoundById, PostNotFoundById, UnauthorizedModificationPost {
        // ARRANGE - Préparation de données
        UserModel user = createUser();
        PostModel post = createPost();

        // Configuration Setter
        user.setId(VALID_USER_ID);
        post.setId(VALID_POST_ID);
        post.setUser(user);

        // Configuration mock
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        when(postRepository.findById(VALID_POST_ID)).thenReturn(Optional.of(post));

        // ACT
        postService.deletePost(VALID_USER_ID, VALID_POST_ID);

        // ASSERT
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("Suppression Publication - UserNotFoundById")
    void deletedPostUserNotFound() {
        assertDeletePostThrowsException(INVALID_USER_ID, VALID_POST_ID,
                UserNotFoundById.class,
                "L'utilisateur avec l'ID " + INVALID_USER_ID + " n'existe pas");
    }
    
    @Test
    @DisplayName("Suppression Publication - PostNotFoundById")
    void deletedPostNotFound() throws PostNotFoundById {
        // ARRANGE - Préparation des données
        UserModel user = createUser();
        user.setId(VALID_USER_ID);

        // Configuration mocks
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        when(postRepository.findById(INVALID_POST_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> postService.deletePost(VALID_USER_ID, INVALID_POST_ID))
                .isInstanceOf(PostNotFoundById.class)
                .hasMessage("La publication n'existe pas");

        // Vérification interractions
        verify(userRepository).findById(VALID_USER_ID);
        verify(postRepository).findById(INVALID_POST_ID);
    }
    
    // ========== TEST REPOSITORY ==========

    @Test
    @DisplayName("Récupération de toutes les publications")
    void displayAllPosts() {
        // ARRANGE - Préparation des données
        PostModel post1 = createPost();
        PostModel post2 = createPost();
        List<PostModel> posts = createListPosts(post1, post2);

        // Configuratiuon mock
        when(postRepository.findAll()).thenReturn(posts);

        // ACT - Appel méthode
        List<PostModel> result = postService.getAll();

        // ASSERT - Vérification des données
        assertNotNull(result);
        assertEquals(posts, result);

        // Vérification interractions
        verify(postRepository).findAll();
    }

    @Test
    @DisplayName("Récupération publication par UserName - valide")
    void getPostByUserName() throws UserNotFoundByUserName {
        // ARRANGE - Préparation des données
        UserModel user = createUser();
        PostModel post1 = createPost();
        PostModel post2 = createPost();
        List<PostModel> posts = createListPosts(post1, post2);

        // Configuration mock
        when(userRepository.existsByUserName(user.getUserName())).thenReturn(true);
        when(postRepository.findByUserUserName(user.getUserName())).thenReturn(posts);

        // ACT - Appel méthode
        List<PostModel> result = postService.getByUserUserName(user.getUserName());

        // ASSERT - Vérification des données
        assertNotNull(result);
        assertEquals(posts, result);

        // Vérification interractions
        verify(userRepository).existsByUserName(user.getUserName());
        verify(postRepository).findByUserUserName(user.getUserName());
    }

    @Test
    @DisplayName("Récupération publication par UserName - UserNotFoundByUserName")
    void getPostByUserNameNotFound() throws UserNotFoundByUserName {
        // ARRANGE - Préparation des données
        UserModel user = createUser();

        // Configuration mock
        when(userRepository.existsByUserName(user.getUserName())).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> postService.getByUserUserName(user.getUserName()))
                .isInstanceOf(UserNotFoundByUserName.class)
                .hasMessage("L'utilisateur " + user.getUserName() + " n'existe pas");

        // Vérification interractions
        verify(userRepository).existsByUserName(user.getUserName());
        verify(postRepository, never()).findByUserUserName(any());
    }

    @Test
    @DisplayName("Récupération publication par UserId - valide")
    void getPostByUserId() throws UserNotFoundById {
        // ARRANGE - <préparation des données
        PostModel post1 = createPost();
        PostModel post2 = createPost();
        List<PostModel> posts = createListPosts(post1, post2);

        // Configuration mocks
        when(userRepository.existsById(VALID_USER_ID)).thenReturn(true);
        when(postRepository.findByUserId(VALID_USER_ID)).thenReturn(posts);

        // ACT - Appel méthode
        List<PostModel> result = postService.getByUserId(VALID_USER_ID);

        // ASSERT - Vérification des données
        assertNotNull(posts);
        assertEquals(posts, result);

        // Vérification interractions
        verify(userRepository).existsById(VALID_USER_ID);
        verify(postRepository).findByUserId(VALID_USER_ID);
    }

    @Test
    @DisplayName("Récupération publication par UserId - UserNotFoundById")
    void getPostByUserIdNotFound() throws UserNotFoundById {
        // ARRANGE - configuration mock
        when(userRepository.existsById(INVALID_USER_ID)).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> postService.getByUserId(INVALID_USER_ID))
            .isInstanceOf(UserNotFoundById.class)
                .hasMessage("L'utilisateur avec l'ID " + INVALID_USER_ID + " n'existe pas");
        
        // Vérification interractions
        verify(userRepository).existsById(INVALID_USER_ID);
        verify(postRepository, never()).findByUserId(INVALID_USER_ID);
    }

    @Test
    @DisplayName("Récupération publication par fishName - valide")
    void getPostByFishName() throws FishNameNotFound {
        //ARRANGE - prpéparation des données
        String fishName = "Carpe";
        PostModel post1 = createPost();
        PostModel post2 = createPost();
        List<PostModel> posts = createListPosts(post1, post2);

        // Configuration mocks
        when(postRepository.existsByFishName(fishName)).thenReturn(true);
        when(postRepository.findByFishName(fishName)).thenReturn(posts);

        // ACT - Appel méthode
        List<PostModel> result = postService.getByFishName(fishName);

        // ASSERT - Vérification des données
        assertNotNull(result);
        assertEquals(posts, result);

        // Vérification interractions
        verify(postRepository).existsByFishName(fishName);
        verify(postRepository).findByFishName(fishName);
    }

    @Test
    @DisplayName("Récupération publication par fishName - FishNameNotFound")
    void getPostByFishNameNotFound() throws FishNameNotFound {
        // ARRANGE - Préparatio ndes données
        String fishName = "notfound";

        // Configuration mock
        when(postRepository.existsByFishName(fishName)).thenReturn(false);

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> postService.getByFishName(fishName))
                .isInstanceOf(FishNameNotFound.class)
                .hasMessage("Ce poisson n'existe pas");

        verify(postRepository).existsByFishName(fishName);
        verify(postRepository, never()).findByFishName(fishName);
    }
}