package com.FishOn.FishOn.Config;

import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

/**
 * Initialise un compte administrateur par défaut
 * LOMBOK UTILISÉ :
 * @RequiredArgsConstructor : Injection par constructeur automatique
 * @Slf4j : Logger automatique
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminDataInitializer {

    private final UserService userService;

    @PostConstruct
    public void initializeAdmin() {
        try {
            // Vérifier si l'admin existe déjà
            userService.getByUserName("admin");
            log.info("Administrateur par défaut déjà existant, initialisation ignorée");

        } catch (Exception e) {
            // L'admin n'existe pas, le créer
            try {
                var adminUser = UserModel.builder()
                        .userName("admin")
                        .email("admin@fishon.com")
                        .firstName("Super")
                        .lastName("Admin")
                        .age(30)
                        .password("Admin123!")
                        .profilePicture("profilePicture/admin.jpg")
                        .isAdmin(true) // Définir comme administrateur
                        .build();

                userService.createAdmin(adminUser);
                log.info("Administrateur par défaut créé avec succès:");
                log.info("  - Username: admin");
                log.info("  - Email: admin@fishon.com");
                log.info("  - Mot de passe: Admin123!");
                log.info("  - Statut: Administrateur");

            } catch (Exception createException) {
                log.error("Erreur lors de la création de l'administrateur par défaut: {}",
                        createException.getMessage());
            }
        }
    }
}