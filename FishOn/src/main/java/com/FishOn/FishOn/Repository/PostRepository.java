package com.FishOn.FishOn.Repository; // Définition espace noms repository, organisation arborescence projet

import com.FishOn.FishOn.Model.PostModel; // Import entité PostModel

import java.util.UUID; // Import classes utilitaires (UUID)

import org.springframework.data.jpa.repository.JpaRepository; // Import interface JpaRepository
import java.util.List; // Import interface List pour collections


// Interface repository qui hérite de JpaRepository
// Génération automatique de l'implémentation par Spring Data JPA
// Types génériques : PostModel (entité) et UUID (type clé primaire)
public interface PostRepository extends JpaRepository<PostModel, UUID> {


    //Méthode de recherche nom d'utilisateur
    // Convention Spring Data JPA : findBy + nom attribut relation
    // Retourne posts d'un utilisateur spécifique
    List <PostModel> findByUserUserName(String userName);

    // Méthode de recherche par ID utilisateur
    // Convention Spring Data JPA : findBy + nom attribut relation
    // Retourne tous les posts d'un utilisateur spécifique
    List<PostModel> findByUserId(UUID id);
    
    // Méthode de recherche par nom de poisson
    // Permet de filtrer les posts par type de poisson attrapé
    List<PostModel> findByFishName(String fishName);
    
    // Méthode de recherche par lieu de pêche
    // Permet de trouver tous les posts d'un lieu spécifique
    List<PostModel> findByLocation(String location);

    // Vérification existance nom de poisson
    // Retourne boolean pour validation nom de poisson
    boolean existsByFishName(String fishName);

    // Vérification existance localisation
    // Retourne boolean pour validation localisation
    boolean existsByLocation(String location);
}