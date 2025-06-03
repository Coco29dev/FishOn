package com.example.API.Peche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.API.Peche.model.User;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    // Signature méthode connexion utilisateur
    User findByUsername(String username);
    // SQL généré
    // SELECT * FROM users WHERE username = 'john'

    // Signature méthode vérification lors de l'inscription
    boolean existsByUsername(String username);
    // SQL généré
    // SELECT COUNT(*) > 0 FROM users WHERE username = 'john'

    // Signature méthode recherche par email
    List<User> findByEmail(String email);
    // SQL généré
    // SELECT * FROM users WHERE email = 'john@email.com';
}