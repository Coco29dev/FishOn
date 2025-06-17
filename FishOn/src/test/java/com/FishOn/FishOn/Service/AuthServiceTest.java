package com.FishOn.FishOn.Service;

import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.UserRepository;
import com.FishOn.FishOn.Exception.FishOnException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserModel testUser;
    private UUID userId;
    private String email;
    private String currentPassword;
    private String newPassword;
    private String encodedCurrentPassword;
    private String encodedNewPassword;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        email = "test@example.com";
        currentPassword = "currentPassword123";
        newPassword = "newPassword456";
        encodedCurrentPassword = "$2a$10$encodedCurrentPassword";
        encodedNewPassword = "$2a$10$encodedNewPassword";

        testUser = new UserModel(
                "testuser",
                email,
                "John",
                "Doe",
                25,
                encodedCurrentPassword, // Mot de passe déjà encodé
                "profile.jpg"
        );
        testUser.setId(userId);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    // =============== TESTS REGISTER ===============

    @Test
    void register_Success() throws EmailAlreadyExists, UserAlreadyExists {
        // Given
        UserModel newUser = new UserModel(
                "newuser",
                "newuser@example.com",
                "Jane",
                "Smith",
                30,
                "plainPassword",
                null
        );

        UserModel createdUser = new UserModel(
                "newuser",
                "newuser@example.com",
                "Jane",
                "Smith",
                30,
                "$2a$10$encodedPassword",
                null
        );
        createdUser.setId(UUID.randomUUID());
        createdUser.setCreatedAt(LocalDateTime.now());
        createdUser.setUpdatedAt(LocalDateTime.now());

        when(userService.createUser(newUser)).thenReturn(createdUser);

        // When
        UserModel result = authService.register(newUser);

        // Then
        assertNotNull(result);
        assertEquals(createdUser.getId(), result.getId());
        assertEquals(createdUser.getUserName(), result.getUserName());
        assertEquals(createdUser.getEmail(), result.getEmail());
        assertEquals(createdUser.getFirstName(), result.getFirstName());
        assertEquals(createdUser.getLastName(), result.getLastName());
        assertEquals(createdUser.getAge(), result.getAge());
        assertEquals(createdUser.getPassword(), result.getPassword());
        assertEquals(createdUser.getProfilePicture(), result.getProfilePicture());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(userService, times(1)).createUser(newUser);
    }

    @Test
    void register_EmailAlreadyExists() throws EmailAlreadyExists, UserAlreadyExists {
        // Given
        UserModel newUser = new UserModel(
                "newuser",
                "existing@example.com",
                "Jane",
                "Smith",
                30,
                "plainPassword",
                null
        );

        when(userService.createUser(newUser)).thenThrow(new EmailAlreadyExists("existing@example.com"));

        // When & Then
        EmailAlreadyExists exception = assertThrows(EmailAlreadyExists.class, () -> {
            authService.register(newUser);
        });

        assertEquals("L'email existing@example.com est déjà pris", exception.getMessage());
        verify(userService, times(1)).createUser(newUser);
    }

    @Test
    void register_UserAlreadyExists() throws EmailAlreadyExists, UserAlreadyExists {
        // Given
        UserModel newUser = new UserModel(
                "existinguser",
                "new@example.com",
                "Jane",
                "Smith",
                30,
                "plainPassword",
                null
        );

        when(userService.createUser(newUser)).thenThrow(new UserAlreadyExists("existinguser"));

        // When & Then
        UserAlreadyExists exception = assertThrows(UserAlreadyExists.class, () -> {
            authService.register(newUser);
        });

        assertEquals("l'usernameexistinguser est déjà pris", exception.getMessage());
        verify(userService, times(1)).createUser(newUser);
    }

    @Test
    void register_WithNullUser() throws EmailAlreadyExists, UserAlreadyExists {
        // Given
        UserModel nullUser = null;

        when(userService.createUser(nullUser)).thenThrow(new IllegalArgumentException("User cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(nullUser);
        });

        verify(userService, times(1)).createUser(nullUser);
    }

    // =============== TESTS UPDATE PASSWORD ===============

    @Test
    void updatePassword_Success() throws UserNotFoundByEmail, InvalidPassword {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);

        // When
        assertDoesNotThrow(() -> {
            authService.updatePassword(email, currentPassword, newPassword);
        });

        // Then
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(currentPassword, encodedCurrentPassword);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(testUser);

        // Vérifier que le mot de passe a été mis à jour
        assertEquals(encodedNewPassword, testUser.getPassword());
    }

    @Test
    void updatePassword_UserNotFound() {
        // Given
        String unknownEmail = "unknown@example.com";
        when(userRepository.findByEmail(unknownEmail)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundByEmail exception = assertThrows(UserNotFoundByEmail.class, () -> {
            authService.updatePassword(unknownEmail, currentPassword, newPassword);
        });

        assertEquals("l'utilisateur unknown@example.com n'existe pas", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(unknownEmail);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    void updatePassword_InvalidCurrentPassword() {
        // Given
        String wrongPassword = "wrongPassword";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(wrongPassword, encodedCurrentPassword)).thenReturn(false);

        // When & Then
        InvalidPassword exception = assertThrows(InvalidPassword.class, () -> {
            authService.updatePassword(email, wrongPassword, newPassword);
        });

        assertEquals("Le mot de passe est incorrect", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(wrongPassword, encodedCurrentPassword);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserModel.class));

        // Vérifier que le mot de passe n'a pas été modifié
        assertEquals(encodedCurrentPassword, testUser.getPassword());
    }

    @Test
    void updatePassword_WithNullEmail() {
        // Given
        String nullEmail = null;

        // When & Then
        assertThrows(Exception.class, () -> {
            authService.updatePassword(nullEmail, currentPassword, newPassword);
        });

        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    void updatePassword_WithEmptyEmail() {
        // Given
        String emptyEmail = "";
        when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundByEmail exception = assertThrows(UserNotFoundByEmail.class, () -> {
            authService.updatePassword(emptyEmail, currentPassword, newPassword);
        });

        assertEquals("l'utilisateur  n'existe pas", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(emptyEmail);
    }

    @Test
    void updatePassword_WithNullCurrentPassword() {
        // Given
        String nullCurrentPassword = null;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(nullCurrentPassword, encodedCurrentPassword)).thenReturn(false);

        // When & Then
        InvalidPassword exception = assertThrows(InvalidPassword.class, () -> {
            authService.updatePassword(email, nullCurrentPassword, newPassword);
        });

        assertEquals("Le mot de passe est incorrect", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(nullCurrentPassword, encodedCurrentPassword);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    void updatePassword_WithNullNewPassword() throws UserNotFoundByEmail, InvalidPassword {
        // Given
        String nullNewPassword = null;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(nullNewPassword)).thenReturn("encodedNull");
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);

        // When
        assertDoesNotThrow(() -> {
            authService.updatePassword(email, currentPassword, nullNewPassword);
        });

        // Then
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(currentPassword, encodedCurrentPassword);
        verify(passwordEncoder, times(1)).encode(nullNewPassword);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updatePassword_WithEmptyCurrentPassword() {
        // Given
        String emptyCurrentPassword = "";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(emptyCurrentPassword, encodedCurrentPassword)).thenReturn(false);

        // When & Then
        InvalidPassword exception = assertThrows(InvalidPassword.class, () -> {
            authService.updatePassword(email, emptyCurrentPassword, newPassword);
        });

        assertEquals("Le mot de passe est incorrect", exception.getMessage());
        verify(passwordEncoder, times(1)).matches(emptyCurrentPassword, encodedCurrentPassword);
    }

    @Test
    void updatePassword_WithEmptyNewPassword() throws UserNotFoundByEmail, InvalidPassword {
        // Given
        String emptyNewPassword = "";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(emptyNewPassword)).thenReturn("encodedEmpty");
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);

        // When
        assertDoesNotThrow(() -> {
            authService.updatePassword(email, currentPassword, emptyNewPassword);
        });

        // Then
        verify(passwordEncoder, times(1)).encode(emptyNewPassword);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updatePassword_SamePassword() throws UserNotFoundByEmail, InvalidPassword {
        // Given - Nouveau mot de passe identique au current
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(currentPassword)).thenReturn("newEncodedSamePassword");
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);

        // When
        assertDoesNotThrow(() -> {
            authService.updatePassword(email, currentPassword, currentPassword);
        });

        // Then
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(currentPassword, encodedCurrentPassword);
        verify(passwordEncoder, times(1)).encode(currentPassword);
        verify(userRepository, times(1)).save(testUser);
    }

    // =============== TESTS EDGE CASES ===============

    @Test
    void updatePassword_VeryLongPassword() throws UserNotFoundByEmail, InvalidPassword {
        // Given
        String veryLongPassword = "A".repeat(1000);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(veryLongPassword)).thenReturn("encodedVeryLongPassword");
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);

        // When
        assertDoesNotThrow(() -> {
            authService.updatePassword(email, currentPassword, veryLongPassword);
        });

        // Then
        verify(passwordEncoder, times(1)).encode(veryLongPassword);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updatePassword_SpecialCharactersPassword() throws UserNotFoundByEmail, InvalidPassword {
        // Given
        String specialPassword = "P@ssw0rd!@#$%^&*()_+{}|:<>?[]\\;'\".,/~`";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(specialPassword)).thenReturn("encodedSpecialPassword");
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);

        // When
        assertDoesNotThrow(() -> {
            authService.updatePassword(email, currentPassword, specialPassword);
        });

        // Then
        verify(passwordEncoder, times(1)).encode(specialPassword);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updatePassword_UnicodePassword() throws UserNotFoundByEmail, InvalidPassword {
        // Given
        String unicodePassword = "Пароль123🔒🔑";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(unicodePassword)).thenReturn("encodedUnicodePassword");
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);

        // When
        assertDoesNotThrow(() -> {
            authService.updatePassword(email, currentPassword, unicodePassword);
        });

        // Then
        verify(passwordEncoder, times(1)).encode(unicodePassword);
        verify(userRepository, times(1)).save(testUser);
    }

    // =============== TESTS COMPORTEMENT REPOSITORY ===============

    @Test
    void updatePassword_RepositorySaveThrowsException() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(UserModel.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.updatePassword(email, currentPassword, newPassword);
        });

        assertEquals("Database error", exception.getMessage());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updatePassword_PasswordEncoderThrowsException() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenThrow(new RuntimeException("Encoding error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.updatePassword(email, currentPassword, newPassword);
        });

        assertEquals("Encoding error", exception.getMessage());
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, never()).save(any(UserModel.class));
    }

    // =============== TESTS VÉRIFICATION ÉTAT ===============

    @Test
    void updatePassword_CheckPasswordIsActuallyUpdated() throws UserNotFoundByEmail, InvalidPassword {
        // Given
        String originalPassword = testUser.getPassword();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel user = invocation.getArgument(0);
            return user;
        });

        // When
        authService.updatePassword(email, currentPassword, newPassword);

        // Then
        assertNotEquals(originalPassword, testUser.getPassword());
        assertEquals(encodedNewPassword, testUser.getPassword());
    }

    @Test
    void updatePassword_MultipleCallsWithSameUser() throws UserNotFoundByEmail, InvalidPassword {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);

        // When
        authService.updatePassword(email, currentPassword, "password1");
        authService.updatePassword(email, "password1", "password2");
        authService.updatePassword(email, "password2", "password3");

        // Then
        verify(userRepository, times(3)).findByEmail(email);
        verify(passwordEncoder, times(3)).matches(anyString(), anyString());
        verify(passwordEncoder, times(3)).encode(anyString());
        verify(userRepository, times(3)).save(testUser);
    }
}