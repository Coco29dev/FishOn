package com.example.API.Peche.service;

// Annotation injection automatique dépendances
import org.springframework.beans.factory.annotation.Autowired;
// Import classe pour encodage
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.example.API.Peche.repository.UserRepository;
import com.example.API.Peche.model.User;

@Service // Dit à Spring classe contenant logique métier
public class UserService {

    @Autowired // Injection automatique
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Méthode création utilisateur
    public User createUser(User user) {

        // Récupération username
        String username = user.getUsername();

        // Vérification unicité username
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Nom utilisateur indisponible");
        }

        // Récupération password
        String password = user.getPassword();
        // Encodage password
        String encodedPassword = passwordEncoder.encode(password);
        // Modification password avec password encoder
        user.setPassword(encodedPassword);

        // Sauvegarde utilisateur
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    // Méthode recherche utilisateur par username
    public User findByUsername(String username) {
        // Recherche utilisateur base
        User user = userRepository.findByUsername(username);

        // Vérification utilisateur existant
        if (user != null) {
            return user;
        } else {
            throw new RuntimeException("Utilisateur introuvable");
        }
    }
}
