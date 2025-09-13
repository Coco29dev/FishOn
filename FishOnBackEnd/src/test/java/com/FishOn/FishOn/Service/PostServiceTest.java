package com.FishOn.FishOn.Service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.when;

import java.lang.reflect.Executable;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.FishOn.FishOn.Exception.FishOnException.MissingTitleException;
import com.FishOn.FishOn.Exception.FishOnException.MissingDescriptionException;
import com.FishOn.FishOn.Exception.FishOnException.MissingFishNameException;
import com.FishOn.FishOn.Exception.FishOnException.MissingPhotoException;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Repository.PostRepository;
import com.FishOn.FishOn.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PostModel postModel;

    @InjectMocks
    PostService postService;

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
                "Magnifique prise du jour",
                "Carpe1",
                "fishPicture1");
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

    @Test
    @DisplayName("Création publication - valide")
    
}