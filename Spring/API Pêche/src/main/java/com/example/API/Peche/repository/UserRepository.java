package com.example.API.Peche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.API.Peche.model.User;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    // Signature méthode connexion utilisateur
    User findByUsername(String username);
    // Signature méthode vérification lors de l'inscription
    boolean existsByUsername(String username);
    // Signature méthode recherche par email
    List<User> findByEmail(String email);
}