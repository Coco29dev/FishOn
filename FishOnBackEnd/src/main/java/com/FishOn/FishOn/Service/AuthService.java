package com.FishOn.FishOn.Service;

import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.UserService;
import com.FishOn.FishOn.Repository.UserRepository;
import org.springframework.stereotype.Service;
import com.FishOn.FishOn.Exception.FishOnException.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Service pour la gestion de l'authentification
 * LOMBOK UTILISÉ :
 * @RequiredArgsConstructor : Injection par constructeur automatique pour les champs final
 * @Slf4j : Logger automatique disponible via log.info(), log.error(), etc.
 */
@Service
@RequiredArgsConstructor // LOMBOK : Remplace @Autowired
@Slf4j // LOMBOK : Logger automatique
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Méthode Register - délègue vers UserService
     */
    public UserModel register(UserModel user) throws EmailAlreadyExists, UserAlreadyExists {
        var createdUser = userService.createUser(user);

        log.info("Nouvel utilisateur enregistré: {} (ID: {})",
                createdUser.getUserName(), createdUser.getId());

        return createdUser;
    }

    /**
     * Méthode changement de mot de passe
     */
    public void updatePassword(String email, String currentPassword, String newPassword)
            throws UserNotFoundByEmail, InvalidPassword {

        var existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmail(email));

        // Vérification du mot de passe actuel
        if (!passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
            log.warn("Tentative de changement de mot de passe avec mot de passe incorrect pour: {}", email);
            throw new InvalidPassword();
        }

        // Mise à jour du mot de passe
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(existingUser);

        log.info("Mot de passe mis à jour pour l'utilisateur: {}", email);
    }
}