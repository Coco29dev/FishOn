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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserModel testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new UserModel(
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                25,
                "password123",
                "profile.jpg"
        );
        testUser.setId(testUserId);
    }

    @Test
    void createUser_Success() throws Exception {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);

        // When
        UserModel result = userService.createUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
    }

    @Test
    void createUser_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExists.class, () -> {
            userService.createUser(testUser);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_UserNameAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUserName("testuser")).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExists.class, () -> {
            userService.createUser(testUser);
        });
    }

    @Test
    void updateUser_Success() throws Exception {
        // Given
        UserModel updatedUser = new UserModel(
                "newusername",
                "newemail@example.com",
                "Jane",
                "Smith",
                30,
                null,
                "newprofile.jpg"
        );

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.existsByUserName("newusername")).thenReturn(false);
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);

        // When
        UserModel result = userService.updateUser(testUserId, updatedUser);

        // Then
        assertNotNull(result);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_UserNotFound() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundById.class, () -> {
            userService.updateUser(testUserId, testUser);
        });
    }

    @Test
    void getByUserName_Success() throws Exception {
        // Given
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(testUser));

        // When
        UserModel result = userService.getByUserName("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getByUserName_NotFound() {
        // Given
        when(userRepository.findByUserName("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundByUserName.class, () -> {
            userService.getByUserName("nonexistent");
        });
    }

    @Test
    void deleteUser_Success() throws Exception {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        userService.deleteUser(testUserId);

        // Then
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_NotFound() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundById.class, () -> {
            userService.deleteUser(testUserId);
        });
    }
}