package com.FishOn.FishOn.Repository; // Définition espace noms repository, organisation arborescence projet

import com.FishOn.FishOn.Model.CommentModel; // Import entité CommentModel

import org.springframework.data.jpa.repository.JpaRepository; // Import interface JpaRepository

import java.util.List; // Import interface List pour collections
import java.util.UUID; // Import UUID pour type clé primaire

// Interface repository pour gestion des commentaires
// Hérite de JpaRepository : génération automatique implémentation par Spring Data JPA
// Types génériques : CommentModel (entité) et UUID (type clé primaire)
public interface CommentRepository extends JpaRepository<CommentModel, UUID> {

    // Méthode de recherche par ID utilisateur
    // Convention Spring Data JPA : findBy + nom attribut relation
    // Retourne tous les commentaires d'un utilisateur spécifique
    List<CommentModel> findByUserId(UUID id);

    // Méthode de recherche par ID post
    // Convention Spring Data JPA : findBy + nom attribut relation
    // Retourne tous les commentaires d'un post spécifique
    List<CommentModel> findByPostId(UUID id);
}