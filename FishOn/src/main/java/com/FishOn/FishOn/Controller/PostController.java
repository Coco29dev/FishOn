package com.FishOn.FishOn.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.server.ResponseStatusException;

import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.DTO.Comment.CommentResponseDTO;
import com.FishOn.FishOn.DTO.Post.PostCreateDTO;
import com.FishOn.FishOn.DTO.Post.PostResponseDTO;
import com.FishOn.FishOn.DTO.Post.PostUpdateDTO;
import com.FishOn.FishOn.Exception.FishOnException.MissingDescriptionException;
import com.FishOn.FishOn.Exception.FishOnException.MissingFishNameException;
import com.FishOn.FishOn.Exception.FishOnException.MissingTitleException;
import com.FishOn.FishOn.Exception.FishOnException.PostNotFoundById;
import com.FishOn.FishOn.Exception.FishOnException.UnauthorizedAccess;
import com.FishOn.FishOn.Exception.FishOnException.UnauthorizedModificationPost;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundByEmail;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundById;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundByUserName;
import com.FishOn.FishOn.Service.PostService;

import jakarta.validation.Valid;

import com.FishOn.FishOn.Model.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



/**
 * Controller REST pour la gestion des publications
 * Gère le fil d'actualité et les opérations CRUD sur les posts
 * 
 * Routes disponibles :
 * - GET /api/posts/feed : Récupération du fil d'actualité (protégé)
 */
@RestController // Annotation Spring : répond automatiquement en JSON
@RequestMapping("/api/posts") // Préfixe pour toutes les routes de ce controller
public class PostController {

    // Injection automatique du service métier pour les opérations sur les posts
    @Autowired
    private PostService postService;

    /**
     * Récupération du fil d'actualité global
     * Endpoint protégé : nécessite une authentification valide
     * 
     * Retourne tous les posts avec leurs commentaires dans l'ordre de création en base
     * 
     * @param authentication Objet d'authentification injecté automatiquement par Spring Security
     * @return List<PostResponseDTO> contenant tous les posts avec leurs commentaires
     * @throws ResponseStatusException 401 si l'utilisateur n'est pas authentifié
     */
    @GetMapping("/feed") // Mapping pour GET /api/posts/feed
    public List<PostResponseDTO> getFeed(Authentication authentication) {

        // Vérification de sécurité : s'assurer que l'utilisateur est bien authentifié
        // Spring Security peut parfois passer un objet Authentication null ou non authentifié
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        // Vérification du type d'authentification : doit être notre CustomUserDetails
        // Protection contre les autres types d'authentification (OAuth, JWT, etc.)
        // Garantit que l'authentification provient de notre système de session
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération de tous les posts depuis la base de données
        // Appel au service qui gère la logique métier et l'accès aux données
        List<PostModel> posts = postService.getAll();

        // Transformation des entités PostModel en DTOs PostResponseDTO
        // Utilisation de Stream API pour une transformation fonctionnelle
        return posts.stream()
                .map(post -> new PostResponseDTO(
                        // Métadonnées du post
                        post.getId(),
                        post.getCreatedAt(),
                        post.getUpdatedAt(),

                        // Contenu principal du post
                        post.getTitle(),
                        post.getDescription(),
                        post.getFishName(),

                        // Données optionnelles de pêche
                        post.getWeight(),
                        post.getLength(),
                        post.getLocation(),
                        post.getCatchDate(),

                        // Nom d'utilisateur qui a créé le post
                        post.getUser().getUserName(),

                        // Conversion des commentaires associés au post
                        // Stream imbriqué pour transformer CommentModel → CommentResponseDTO
                        post.getComments().stream()
                                .map(comment -> new CommentResponseDTO(
                                        comment.getId(),
                                        comment.getContent(),
                                        comment.getCreatedAt(),
                                        comment.getUpdatedAt(),
                                        comment.getUser().getUserName())) // Nom d'utilisateur qui a écrit le commentaire
                                .collect(Collectors.toList()))) // Collecte des commentaires en List<CommentResponseDTO>
                .collect(Collectors.toList()); // Collecte finale en List<PostResponseDTO>
    }
    
    @GetMapping("/{userName}")
    public List<PostResponseDTO> getPostsByUserName(Authentication authentication,
            @PathVariable String userName) throws UserNotFoundByUserName {

        // Vérification de sécurité : s'assurer que l'utilisateur est bien authentifié
        // Spring Security peut parfois passer un objet Authentication null ou non authentifié
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        // Vérification du type d'authentification : doit être notre CustomUserDetails
        // Protection contre les autres types d'authentification (OAuth, JWT, etc.)
        // Garantit que l'authentification provient de notre système de session
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération de tout les posts d'un utilisateur
        List<PostModel> postsUser = postService.getByUserUserName(userName);

        // Transformation des entités PostModel en DTOs PostResponseDTO
        // Utilisation de Stream API pour une transformation fonctionnelle
        return postsUser.stream()
                .map(post -> new PostResponseDTO(
                        // Métadonnées du post
                        post.getId(),
                        post.getCreatedAt(),
                        post.getUpdatedAt(),

                        // Contenu principal du post
                        post.getTitle(),
                        post.getDescription(),
                        post.getFishName(),

                        // Données optionnelles de pêche
                        post.getWeight(),
                        post.getLength(),
                        post.getLocation(),
                        post.getCatchDate(),

                        // Nom d'utilisateur qui a créé le post
                        post.getUser().getUserName(),

                        // Conversion des commentaires associés au post
                        // Stream imbriqué pour transformer CommentModel → CommentResponseDTO
                        post.getComments().stream()
                                .map(comment -> new CommentResponseDTO(
                                        comment.getId(),
                                        comment.getContent(),
                                        comment.getCreatedAt(),
                                        comment.getUpdatedAt(),
                                        comment.getUser().getUserName())) // Nom d'utilisateur qui a écrit le commentaire
                                .collect(Collectors.toList()))) // Collecte des commentaires en List<CommentResponseDTO>
                .collect(Collectors.toList()); // Collecte finale en List<PostResponseDTO>
    }

    @PostMapping
    public PostResponseDTO createPost(@Valid @RequestBody PostCreateDTO postCreateDTO, Authentication authentication)
        throws MissingTitleException, MissingDescriptionException, MissingFishNameException, UserNotFoundById
    {
        // Vérification de sécurité identique à getUser()
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        // Vérification du type d'authentification : doit être notre CustomUserDetails
        // Protection contre les autres types d'authentification (OAuth, JWT, etc.)
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération de l'ID utilisateur depuis l'authentification
        // Garantit que l'utilisateur ne peut créer sa publication et non celle des autres
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUser().getId();

        // Conversion objet PostCreateDTO en objet PostModel
        PostModel post = new PostModel(
                // Champ obligatoire
                postCreateDTO.getTitle(),
                postCreateDTO.getDescription(),
                postCreateDTO.getFishName());

        // Champ optionnel
        post.setWeight(postCreateDTO.getWeight());
        post.setLength(postCreateDTO.getLength());
        post.setLocation(postCreateDTO.getLocation());
        post.setCatchDate(postCreateDTO.getCatchDate());

        // Création publication
        PostModel createPost = postService.createPost(currentUserId, post);

        return new PostResponseDTO(
                // Métadonnées du post
                createPost.getId(),
                createPost.getCreatedAt(),
                createPost.getUpdatedAt(),

                // Contenu principal du post
                createPost.getTitle(),
                createPost.getDescription(),
                createPost.getFishName(),

                // Données optionnelles de pêche
                createPost.getWeight(),
                createPost.getLength(),
                createPost.getLocation(),
                createPost.getCatchDate(),

                // Nom d'utilisateur qui a créé le post
                createPost.getUser().getUserName(),

                createPost.getComments().stream()
                        .map(comment -> new CommentResponseDTO(
                                comment.getId(),
                                comment.getContent(),
                                comment.getCreatedAt(),
                                comment.getUpdatedAt(),
                                comment.getUser().getUserName() // Nom d'utilisateur qui a écrit le commentaire
                        ))
                        .collect(Collectors.toList()) // Collecte des commentaires en List<CommentResponseDTO>
        );
    }

    @PutMapping("/{postId}")
    public PostResponseDTO updatePost(@PathVariable UUID postId, @Valid @RequestBody PostUpdateDTO postUpdateDTO,
            Authentication authentication)
            throws MissingTitleException, MissingDescriptionException, MissingFishNameException,
            UserNotFoundById, PostNotFoundById, UnauthorizedModificationPost
    {
        // Vérification de sécurité identique à getUser()
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        // Vérification du type d'authentification : doit être notre CustomUserDetails
        // Protection contre les autres types d'authentification (OAuth, JWT, etc.)
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération de l'ID utilisateur depuis l'authentification
        // Garantit que l'utilisateur ne peut créer sa publication et non celle des autres
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUser().getId();

        // Conversion objet PostCreateDTO en objet PostModel
        PostModel post = new PostModel(
                // Champ obligatoire
                postUpdateDTO.getTitle(),
                postUpdateDTO.getDescription(),
                postUpdateDTO.getFishName());

        // Champ optionnel
        post.setWeight(postUpdateDTO.getWeight());
        post.setLength(postUpdateDTO.getLength());
        post.setLocation(postUpdateDTO.getLocation());
        post.setCatchDate(postUpdateDTO.getCatchDate());

        // MAJ publication
        PostModel updatePost = postService.updatePost(currentUserId, postId, post);

        return new PostResponseDTO(
                // Métadonnées du post
                updatePost.getId(),
                updatePost.getCreatedAt(),
                updatePost.getUpdatedAt(),

                // Contenu principal du post
                updatePost.getTitle(),
                updatePost.getDescription(),
                updatePost.getFishName(),

                // Données optionnelles de pêche
                updatePost.getWeight(),
                updatePost.getLength(),
                updatePost.getLocation(),
                updatePost.getCatchDate(),

                // Nom d'utilisateur qui a créé le post
                updatePost.getUser().getUserName(),

                updatePost.getComments().stream()
                        .map(comment -> new CommentResponseDTO(
                                comment.getId(),
                                comment.getContent(),
                                comment.getCreatedAt(),
                                comment.getUpdatedAt(),
                                comment.getUser().getUserName() // Nom d'utilisateur qui a écrit le commentaire
                        ))
                        .collect(Collectors.toList()) // Collecte des commentaires en List<CommentResponseDTO>
        );
    }
    
    @DeleteMapping("/{postId}")
    public String deletePost(@PathVariable UUID postId, Authentication authentication)
            throws PostNotFoundById, UserNotFoundById, UnauthorizedModificationPost
    {
        // Vérification de sécurité identique à getUser()
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        // Vérification du type d'authentification : doit être notre CustomUserDetails
        // Protection contre les autres types d'authentification (OAuth, JWT, etc.)
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération de l'ID utilisateur depuis l'authentification
        // Garantit que l'utilisateur ne peut créer sa publication et non celle des autres
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUser().getId();

        // Appel logique métier suppression publication
        postService.deletePost(currentUserId, postId);

        return "Publication supprimé";
    }
}