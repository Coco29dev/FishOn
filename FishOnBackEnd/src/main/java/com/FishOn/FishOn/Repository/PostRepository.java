package com.FishOn.FishOn.Repository;

import com.FishOn.FishOn.Model.PostModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface PostRepository extends JpaRepository<PostModel, UUID> {

    // ========== REQUÊTES AVEC FETCH JOIN POUR ÉVITER LazyInitializationException ==========

    /**
     * Récupération de tous les posts avec leurs commentaires et utilisateurs
     * FETCH JOIN charge immédiatement toutes les relations nécessaires
     */
    @Query("SELECT DISTINCT p FROM PostModel p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH p.user " +
            "ORDER BY p.createdAt DESC")
    List<PostModel> findAllWithCommentsAndUsers();

    /**
     * Récupération des posts d'un utilisateur avec commentaires
     */
    @Query("SELECT DISTINCT p FROM PostModel p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH p.user u " +
            "WHERE u.userName = :userName " +
            "ORDER BY p.createdAt DESC")
    List<PostModel> findByUserUserNameWithComments(@Param("userName") String userName);

    /**
     * Récupération des posts d'un utilisateur par ID avec commentaires
     */
    @Query("SELECT DISTINCT p FROM PostModel p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH p.user u " +
            "WHERE u.id = :userId " +
            "ORDER BY p.createdAt DESC")
    List<PostModel> findByUserIdWithComments(@Param("userId") UUID userId);

    /**
     * Récupération d'un post par ID avec tous ses commentaires
     */
    @Query("SELECT DISTINCT p FROM PostModel p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH p.user " +
            "WHERE p.id = :postId")
    PostModel findByIdWithComments(@Param("postId") UUID postId);

    // ========== MÉTHODES ORIGINALES CONSERVÉES ==========

    List<PostModel> findByUserUserName(String userName);
    List<PostModel> findByUserId(UUID id);
    List<PostModel> findByFishName(String fishName);
    List<PostModel> findByLocation(String location);
    boolean existsByFishName(String fishName);
    boolean existsByLocation(String location);
}