package com.FishOn.FishOn.Service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.FishOn.FishOn.Exception.FishOnException.EmailAlreadyExists;
import com.FishOn.FishOn.Exception.FishOnException.InvalidPassword;
import com.FishOn.FishOn.Exception.FishOnException.UserAlreadyExists;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundByEmail;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // ========== CONSTANTES ==========
    private static final String VALID_EMAIL = "user@fishon.com";
    private static final String INVALID_EMAIL = "invalid@fishon.com";
    private static final String CURRENT_PASSWORD = "currentPassword";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String WRONG_PASSWORD = "wrongPassword";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @Mock
    UserRepository userRepository;

    @Mock
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    // ========== FONCTION HELPER ==========
    private UserModel createUser() {
        return new UserModel(
                "user1",
                VALID_EMAIL,
                "J",
                "D",
                25,
                ENCODED_PASSWORD,
                "profile.jpg");
    }

    // ========== TESTS MÉTHODE REGISTER ==========

    @Test
    @DisplayName("Inscription utilisateur - valide")
    void register() throws EmailAlreadyExists, UserAlreadyExists {
        // ARRANGE - Préparation des données
        UserModel user = createUser();
        UserModel savedUser = createUser();

        // Configuration mock
        when(userService.createUser(user)).thenReturn(savedUser);

        // ACT
        UserModel result = authService.register(user);

        // ASSERT - Vérification des données
        assertNotNull(result);
        assertEquals(savedUser, result);

        // Vérification interactions
        verify(userService).createUser(user);
    }

    @Test
    @DisplayName("Inscription utilisateur - EmailAlreadyExists")
    void registerEmailAlreadyExists() throws EmailAlreadyExists, UserAlreadyExists {
        // ARRANGE - Préparation des données
        UserModel user = createUser();

        // Configuration mock
        when(userService.createUser(user)).thenThrow(new EmailAlreadyExists(user.getEmail()));

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> authService.register(user))
                .isInstanceOf(EmailAlreadyExists.class)
                .hasMessage("L'email " + user.getEmail() + " est déjà pris");

        // Vérification interactions
        verify(userService).createUser(user);
    }

    @Test
    @DisplayName("Inscription utilisateur - UserAlreadyExists")
    void registerUserAlreadyExists() throws EmailAlreadyExists, UserAlreadyExists {
        // ARRANGE - Préparation des données
        UserModel user = createUser();

        // Configuration mock
        when(userService.createUser(user)).thenThrow(new UserAlreadyExists(user.getUserName()));

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> authService.register(user))
                .isInstanceOf(UserAlreadyExists.class)
                .hasMessage("L'username " + user.getUserName() + " est déjà pris");

        // Vérification interactions
        verify(userService).createUser(user);
    }

    // ========== TESTS MÉTHODE UPDATE PASSWORD ==========

    @Test
    @DisplayName("Changement mot de passe - valide")
    void updatePassword() throws UserNotFoundByEmail, InvalidPassword {
        // ARRANGE - Préparation des données
        UserModel existingUser = createUser();
        existingUser.setPassword(ENCODED_PASSWORD);

        // Configuration mocks
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(CURRENT_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn("newEncodedPassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // ACT
        authService.updatePassword(VALID_EMAIL, CURRENT_PASSWORD, NEW_PASSWORD);

        // ASSERT - Vérification interactions
        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(passwordEncoder).matches(CURRENT_PASSWORD, ENCODED_PASSWORD);
        verify(passwordEncoder).encode(NEW_PASSWORD);
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Changement mot de passe - UserNotFoundByEmail")
    void updatePasswordUserNotFound() {
        // ARRANGE - Configuration mock
        when(userRepository.findByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> authService.updatePassword(INVALID_EMAIL, CURRENT_PASSWORD, NEW_PASSWORD))
                .isInstanceOf(UserNotFoundByEmail.class)
                .hasMessage("L'utilisateur " + INVALID_EMAIL + " n'existe pas");

        // Vérification interactions
        verify(userRepository).findByEmail(INVALID_EMAIL);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Changement mot de passe - InvalidPassword")
    void updatePasswordInvalidPassword() {
        // ARRANGE - Préparation des données
        UserModel existingUser = createUser();

        // Configuration mocks
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(WRONG_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> authService.updatePassword(VALID_EMAIL, WRONG_PASSWORD, NEW_PASSWORD))
                .isInstanceOf(InvalidPassword.class)
                .hasMessage("Le mot de passe est incorrect");

        // Vérification interactions
        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(passwordEncoder).matches(WRONG_PASSWORD, ENCODED_PASSWORD);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }
}