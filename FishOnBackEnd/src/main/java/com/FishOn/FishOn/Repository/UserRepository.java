package com.FishOn.FishOn.Repository; // Définition espace noms repository, organisation arborescence projet

import com.FishOn.FishOn.Model.UserModel; // Import entité Usermodel

import org.springframework.data.jpa.repository.JpaRepository; // Import interface JpaRepository

import java.util.List; // Import interface List pour collections
import java.util.Optional; // Import Optional pour gestion valeurs nulles
import java.util.UUID; // Import UUID pour type clé primaire

// Interface repository pour gestion utilisateur
// Génération automatique de l'implémentation par Spring Data JPA
// Types génériques : UserModel (entité) et UUID (type clé primaire)
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    // Recherche utilisateur par nom d'utilisateur
    // Optional : peut retourner null si utilisateur inexistant
    // Utilisé pour authentification et vérification unicité
    Optional<UserModel> findByUserName(String userName);

    // Recherche utilisateur par email
    // Optional : gestion sécurisée des valeurs nulles  
    // Utilisé pour connexion par email et récupération mot de passe
    Optional<UserModel> findByEmail(String email);

    // Vérification existence d'un nom d'utilisateur
    // Retourne boolean : true si existe, false sinon
    // Utilisé lors inscription pour éviter doublons
    boolean existsByUserName(String userName);

    // Vérification existence d'un email
    // Retourne boolean pour validation unicité
    // Utilisé lors inscription et modification profil
    boolean existsByEmail(String email);

}