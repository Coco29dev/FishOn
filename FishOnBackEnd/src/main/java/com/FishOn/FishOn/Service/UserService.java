package com.FishOn.FishOn.Service;

import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.UserRepository;
import com.FishOn.FishOn.Exception.FishOnException.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des utilisateurs
 * LOMBOK UTILISÉ :
 * @RequiredArgsConstructor : Injection par constructeur automatique pour les champs final
 * @Slf4j : Logger automatique disponible via log.info(), log.error(), etc.
 */
@Service
@RequiredArgsConstructor // LOMBOK : Remplace @Autowired
@Slf4j // LOMBOK : Logger automatique
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // =============== MÉTHODES CRUD STANDARD ===============

    public UserModel createUser(UserModel user) throws EmailAlreadyExists, UserAlreadyExists {
        // Vérification unicité email
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Tentative de création avec email déjà existant: {}", user.getEmail());
            throw new EmailAlreadyExists(user.getEmail());
        }

        // Vérification unicité username
        if (userRepository.existsByUserName(user.getUserName())) {
            log.warn("Tentative de création avec username déjà existant: {}", user.getUserName());
            throw new UserAlreadyExists(user.getUserName());
        }

        // Encodage du mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        var savedUser = userRepository.save(user);

        log.info("Nouvel utilisateur créé: {} (Admin: {})",
                savedUser.getUserName(), savedUser.isAdmin());

        return savedUser;
    }

    public UserModel updateUser(UUID userId, UserModel updatedUser)
            throws UserNotFoundById, EmailAlreadyExists, UserAlreadyExists {

        // Récupération utilisateur existant
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        // Vérification email si changé
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new EmailAlreadyExists(updatedUser.getEmail());
            }
        }

        // Vérification username si changé
        if (!existingUser.getUserName().equals(updatedUser.getUserName())) {
            if (userRepository.existsByUserName(updatedUser.getUserName())) {
                throw new UserAlreadyExists(updatedUser.getUserName());
            }
        }

        // Mise à jour des champs
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUserName(updatedUser.getUserName());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setProfilePicture(updatedUser.getProfilePicture());

        var savedUser = userRepository.save(existingUser);

        log.info("Utilisateur mis à jour: {} (ID: {})",
                savedUser.getUserName(), savedUser.getId());

        return savedUser;
    }

    public void deleteUser(UUID userId) throws UserNotFoundById {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        userRepository.delete(user);

        log.info("Utilisateur supprimé: {} (ID: {})", user.getUserName(), userId);
    }

    // =============== MÉTHODES DE RECHERCHE ===============

    public UserModel getByUserName(String userName) throws UserNotFoundByUserName {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundByUserName(userName));
    }

    public UserModel getByEmail(String email) throws UserNotFoundByEmail {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmail(email));
    }

    public boolean userNameExists(String userName) throws UserNotFoundByUserName {
        if (!userRepository.existsByUserName(userName)) {
            throw new UserNotFoundByUserName(userName);
        }
        return true;
    }

    public boolean emailExists(String email) throws UserNotFoundByEmail {
        if (!userRepository.existsByEmail(email)) {
            throw new UserNotFoundByEmail(email);
        }
        return true;
    }

    // =============== MÉTHODES ADMIN UNIQUEMENT ===============

    /**
     * Récupère tous les utilisateurs (ADMIN UNIQUEMENT)
     * Cette méthode est appelée uniquement depuis les controllers admin
     */
    public List<UserModel> getAllUsers() {
        var users = userRepository.findAll();

        log.info("Récupération de tous les utilisateurs: {} utilisateurs trouvés", users.size());

        return users;
    }

    /**
     * Créer un administrateur (méthode utilitaire pour l'initialisation)
     */
    public UserModel createAdmin(UserModel adminUser) throws EmailAlreadyExists, UserAlreadyExists {
        // Vérifications standard
        if (userRepository.existsByEmail(adminUser.getEmail())) {
            throw new EmailAlreadyExists(adminUser.getEmail());
        }

        if (userRepository.existsByUserName(adminUser.getUserName())) {
            throw new UserAlreadyExists(adminUser.getUserName());
        }

        // Forcer le statut admin
        adminUser.setIsAdmin(true);
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));

        var savedAdmin = userRepository.save(adminUser);

        log.info("Nouvel administrateur créé: {} (ID: {})",
                savedAdmin.getUserName(), savedAdmin.getId());

        return savedAdmin;
    }
}