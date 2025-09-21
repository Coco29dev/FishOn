package com.FishOn.FishOn.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.FishOn.FishOn.Model.UserModel;

@DataJpaTest // Slice qui charge seulement la couche JPA
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Interface Test/BDD, contrôle des données

    @Autowired
    private UserRepository userRepository;

    // ========== MÉTHODE HELPER ==========

    private UserModel createUser() {
        return new UserModel(
                "User",
                "user@fishon.com",
                "J",
                "D",
                25,
                "encodePassword",
                "porfilePicture");
    }
    // ========== MÉTHODE REPOSITORY ==========

    @Test
    @DisplayName("Recherche par userName - valide")

    void userFindByUserName() {
        // ARRANGE - Préparation des données
        UserModel user = createUser();
        entityManager.persistAndFlush(user); // Sauvegarde l'écriture en BDD

        // ACT - Appel méthode
        Optional<UserModel> result = userRepository.findByUserName(user.getUserName());

        // ASSERT - Vérification des résultats
        assertThat(result).isPresent();
        assertThat(result.get().getUserName()).isEqualTo(user.getUserName());
        assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Recherche par userName - null")
    void userNotFoundByUserNameNull() {
        // ACT
        Optional<UserModel> result = userRepository.findByUserName(null);

        // Assert - Vérification données invalide
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Recherche par userName - invalide")
    void userNotFoundByUserName() {
        // ACT
        Optional<UserModel> result = userRepository.findByUserName("invalide");

        // Assert - Vérification données invalide
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Recherche par email - valide")
    void userFindByEmail() {
        // ARRANGE - Préparation des données
        UserModel user = createUser();
        entityManager.persistAndFlush(user); // Sauvegarde l'écriture en BDD

        // ACT - Appel méthode
        Optional<UserModel> result = userRepository.findByEmail(user.getEmail());

        // ASSERT - Vérification des résultats
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(result.get().getUserName()).isEqualTo(user.getUserName());
    }

    @Test
    @DisplayName("Recherche par email - null")
    void userNotFoundByEmailNull() {
        // ACT
        Optional<UserModel> result = userRepository.findByEmail(null);

        // Assert - Vérification données invalide
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Recherche par email - invalide")
    void userNotFoundByEmail() {
        // ACT
        Optional<UserModel> result = userRepository.findByEmail("invalide");

        // Assert - Vérification données invalide
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Vérification existance userName - valide")
    void existByUserName() {
        // ARRANGE - Préparation des données
        UserModel user = createUser();
        entityManager.persistAndFlush(user); // Sauvegarde l'écriture en BDD

        // ACT
        boolean result = userRepository.existsByUserName(user.getUserName());

        // ASSERT
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Vérification existance userName - null")
    void notExistByUserNameNull() {
        // ACT
        boolean result = userRepository.existsByUserName(null);

        // ASSERT
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Vérification existance userName - invalide")
    void notExistByUserName() {
        // ACT
        boolean result = userRepository.existsByUserName("invalide");

        // ASSERT
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Vérification existance email - valide")
    void existByEmail() {
        // ARRANGE - Préparation des données
        UserModel user = createUser();
        entityManager.persistAndFlush(user); // Sauvegarde l'écriture en BDD

        // ACT
        boolean result = userRepository.existsByEmail(user.getEmail());

        // ASSERT
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Vérification existance email - null")
    void notExistByEmailNull() {
        // ACT
        boolean result = userRepository.existsByEmail(null);

        // ASSERT
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Vérification existance email - invalide")
    void notExistByEmail() {
        // ACT
        boolean result = userRepository.existsByEmail("invalide");

        // ASSERT
        assertThat(result).isFalse();
    }
}