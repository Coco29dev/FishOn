package com.FishOn.FishOn.Service;

import org.junit.jupiter.api.Test; // Création des tests
import org.junit.jupiter.api.DisplayName; // Nommage tests
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*; // Outil de simulation
import org.mockito.InjectMocks; // Injection des mocks
import org.mockito.Mock; // Création objets simulés
import static org.assertj.core.api.Assertions.*; // Outil de vérification
import static org.junit.Assert.assertNotNull;

import java.util.Optional;
import java.util.UUID;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.FishOn.FishOn.Repository.UserRepository;
import com.FishOn.FishOn.Service.UserService;
import com.FishOn.FishOn.Model.UserModel;

import com.FishOn.FishOn.Exception.FishOnException.EmailAlreadyExists;
import com.FishOn.FishOn.Exception.FishOnException.UserAlreadyExists;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundByEmail;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundById;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundByUserName;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock // Simule UserRepository
    private UserRepository userRepository;

    @Mock // Simule PasswordEncoder
    private PasswordEncoder passwordEncoder;

    @InjectMocks // Injecte les mocks dans UserService
    private UserService userService;

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

    // ========== TEST MÉTHODE CRUD ==========

    // ========== TEST CRÉATION UTILISATEUR ==========

    @Test
    @DisplayName("Création utilisateur valide")
    void createValidUser() throws EmailAlreadyExists, UserAlreadyExists {
        // ARRANGE - Préparation données et mocks
        UserModel user = createUser();
        String encodePassword = "psswd_123"; // Simulation mot de passe encodé

        // Configuration mocks
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUserName(user.getUserName())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodePassword);
        when(userRepository.save(any(UserModel.class))).thenReturn(user);

        // ACT - Appel de la méthode à tester
        UserModel result = userService.createUser(user);

        // ASSERT - Vérifications
        assertThat(result).isNotNull(); // Vérification résultat n'est pas null
        assertThat(result.getEmail()).isEqualTo(user.getEmail()); // vérification email

        // Vérifier que les bonnes méthodes ont été appelées
        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).existsByUserName(user.getUserName());
        verify(passwordEncoder).encode("userpassword");
        // Vérification que la méthode est appelé avec un UserModel
        verify(userRepository).save(any(UserModel.class));
    }
    
    @Test
    @DisplayName("Création utilisateur - EmailAlreadyExist")
    void createdEmailAlreadyExist() throws EmailAlreadyExists {
        // ARANGE
        UserModel user = createUser();
        // Simulation email existant
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // ACT & ASSERT
        // assertThatThrownBy : Fonction lançant une exception
        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(EmailAlreadyExists.class) // Vérification type de l'exception
                .hasMessage("L'email user1@fishon.com est déjà pris"); // Vérification message de l'exception
        verify(userRepository, never()).save(any(UserModel.class)); // Vérification que la méthode de sauvegarde n'est jamais appelée
    }
    
    @Test
    @DisplayName("Création utilisateur - UserAlreadyExist")
    void createUserAlreadyExist() throws UserAlreadyExists {
        // ARRANGE
        UserModel user = createUser();
        // Simulation username déjà existant
        when(userRepository.existsByUserName(user.getUserName())).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(UserAlreadyExists.class)
                .hasMessage("L'username user1 est déjà pris");
        verify(userRepository, never()).save(any(UserModel.class));
    }

    // ========== TEST MAJ UTILISATEUR ==========

    @Test
    @DisplayName("MAJ utilisateur - valide")
    void updateValidUser() throws UserNotFoundById, EmailAlreadyExists, UserAlreadyExists {
        // Préparation des données
        UserModel user = createUser();
        UUID userId = UUID.randomUUID();
        UserModel updatedUser = new UserModel(
            "newUser",
            "newuser@fishon.com",
            "Ja",
            "Do",
            30,
            null,
            ""
        );


        // Configuration mocks
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // findById : retourne toujours un Optional(Type standard Java)
        // Optional.of() : créer un Optional contenant l'objet désiré 
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(false);
        when(userRepository.existsByUserName(updatedUser.getUserName())).thenReturn(false);
        when(userRepository.save(any(UserModel.class))).thenReturn(updatedUser);

        // ACT - Appel méthodes à tester
        UserModel result = userService.updateUser(userId, updatedUser);

        // ASSERT - Vérification données
        assertNotNull(result);
        assertThat(result.getUserName()).isEqualTo("newUser");
        assertThat(result.getEmail()).isEqualTo("newuser@fishon.com");
        assertThat(result.getFirstName()).isEqualTo("Ja");
        assertThat(result.getLastName()).isEqualTo("Do");
        assertThat(result.getAge()).isEqualTo(30);

        // ASSERT - Vérification méthodes
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(updatedUser.getEmail());
        verify(userRepository).existsByUserName(updatedUser.getUserName());
        verify(userRepository).save(any(UserModel.class));
    }

    @Test
    @DisplayName("MAJ utilisateur - UserNotFoundById")
    void updateUserNotFound() throws UserNotFoundById {
        // Préparation des données
        UUID userId = UUID.randomUUID();
        UserModel updatedUser = new UserModel(
                "newUser",
                "newuser@fishon.com",
                "Ja",
                "Do",
                30,
                null,
                "");

        // Configuration mock
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // ACT & ASSERT - Lancement exception
        assertThatThrownBy(() -> userService.updateUser(userId, updatedUser))
                .isInstanceOf(UserNotFoundById.class)
                .hasMessage("L'utilisateur avec l'ID " + userId + " n'existe pas");
        verify(userRepository, never()).save(any(UserModel.class));
    }
    
    @Test
    @DisplayName("MAJ utilisateur - EmailAlreadyExist")
    void updateEmailAlreadyExist() throws EmailAlreadyExists {
        // Préparation des données
        UserModel user = createUser();
        UUID userId = UUID.randomUUID();
        UserModel updatedUser = new UserModel(
                "newUser",
                "newuser@fishon.com",
                "Ja",
                "Do",
                30,
                null,
                "");

        // Configuration mock
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(true);

        // ASSERT - Lancement exception
        assertThatThrownBy(() -> userService.updateUser(userId, updatedUser))
                .isInstanceOf(EmailAlreadyExists.class)
                .hasMessage("L'email newuser@fishon.com est déjà pris");
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    @DisplayName("MAJ utilisateur - UserAlreadyExist")
    void updateUserAlreadyExist() {
        // Préparation des données
        UserModel user = createUser();
        UUID userId = UUID.randomUUID();
        UserModel updatedUser = new UserModel(
                "newUser",
                "newuser@fishon.com",
                "Ja",
                "Do",
                30,
                null,
                "");

        // Configuration mock
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByUserName(updatedUser.getUserName())).thenReturn(true);

        // ASSERT - Lancement exception
        assertThatThrownBy(() -> userService.updateUser(userId, updatedUser))
                .isInstanceOf(UserAlreadyExists.class)
                .hasMessage("L'username newUser est déjà pris");
        verify(userRepository, never()).save(any(UserModel.class));
    }
    
    // ========== DELETE UTILISATEUR ==========

    @Test
    @DisplayName("Suppression utilisateur")
    void deleteUser() throws UserNotFoundById {
        // Préparation des données
        UUID userId = UUID.randomUUID();
        UserModel user = createUser();

        // Configuration mock
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // ACT - Appel méthode
        userService.deleteUser(userId);

        // ASSERT - Vérification
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Suppression utilisateur - UserNotFoundById")
    void deleteUserNotFoundById() throws UserNotFoundById {
        // Préparation données
        UUID userId = UUID.randomUUID();
        UserModel user = createUser();

        // Configuration mock
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // ASSERT - Lancement exceptions
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(UserNotFoundById.class)
                .hasMessage("L'utilisateur avec l'ID " + userId + " n'existe pas");
        verify(userRepository, never()).delete(user);
    }

    // ========== TEST MÉTHODE REPOSITORY ==========

    // ========== TEST UTILISATEUR TROUVÉ ==========

    @Test
    @DisplayName("Utilisateur trouvé")
    void getUserByUserName() throws UserNotFoundByUserName {
        // Préparation données
        UserModel user = createUser();

        // Configuration mock
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

        // ACT - Appel méthode
        UserModel result = userService.getByUserName(user.getUserName());

        // ASSERT - vérification données
        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo(user.getUserName());
    }

    @Test
    @DisplayName("Utilisateur trouvé - UserNotFoundByUserName")
    void userNotFoundByUserName() throws UserNotFoundByUserName {
        // Préparation des données
        String userName = "unknowUser";

        // Configuration mock
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // ASSERT - Lancement exception
        assertThatThrownBy(() -> userService.getByUserName(userName))
                .isInstanceOf(UserNotFoundByUserName.class)
                .hasMessage("L'utilisateur " + userName + " n'existe pas");
    }
    
    // ========== TEST EMAIL TROUVÉ ==========

    @Test
    @DisplayName("Email trouvé")
    void getByEmail() throws UserNotFoundByEmail {
        // Préparation des données
        UserModel user = createUser();

        // Configuration mock
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // ACT - Appel méthode
        UserModel result = userService.getByEmail(user.getEmail());

        // ASSERT - vérification des données
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Email trouvé - UserNotFoundByEmail")
    void userNotFoundByEmail() throws UserNotFoundByEmail {
        // Préparation des données
        String email = "unknowEmail";

        // Configuration mock
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // ASSERT - Lancement exception
        assertThatThrownBy(() -> userService.getByEmail(email))
                .isInstanceOf(UserNotFoundByEmail.class)
                .hasMessage("L'utilisateur " + email + " n'existe pas");
    }

    // ========== TEST NOM UTILISATEUR EXISTE ==========

    @Test
    @DisplayName("Nom utilisateur existe")
    void userNameExistTrue() throws UserNotFoundByUserName {
        // Préparation données
        String userName = "user";

        // Configuration mock
        when(userRepository.existsByUserName(userName)).thenReturn(true);

        // ACT - Appel méthode
        boolean result = userService.userNameExists(userName);

        // ASSERT - vérification données
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Nom utilisateur n'existe pas")
    void userNameExistFalse() throws UserNotFoundByUserName {
        // Préparation données
        String userName = "user";

        // Configuration mock
        when(userRepository.existsByUserName(userName)).thenReturn(false);

        // ASSERT - Lancement exception
        assertThatThrownBy(() -> userService.userNameExists(userName))
                .isInstanceOf(UserNotFoundByUserName.class)
                .hasMessage("L'utilisateur " + userName + " n'existe pas");
    }
    
    // ========== TEST EMAIL EXISTE ==========

    @Test
    @DisplayName("email existe")
    void emailExistsTrue() throws UserNotFoundByEmail {
        String email = "user1@fishon.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
    
        boolean result = userService.emailExists(email);
    
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("email inexistant")
    void emailExistsFalse() throws UserNotFoundByEmail {
        String email = "unknown@fishon.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
    
        assertThatThrownBy(() -> userService.emailExists(email))
            .isInstanceOf(UserNotFoundByEmail.class)
            .hasMessage("L'utilisateur " + email + " n'existe pas");
    }
}