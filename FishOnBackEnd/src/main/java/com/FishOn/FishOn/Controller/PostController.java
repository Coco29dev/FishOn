package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.DTO.Comment.CommentResponseDTO;
import com.FishOn.FishOn.DTO.Post.PostCreateDTO;
import com.FishOn.FishOn.DTO.Post.PostResponseDTO;
import com.FishOn.FishOn.DTO.Post.PostUpdateDTO;
import com.FishOn.FishOn.Exception.FishOnException.*;
import com.FishOn.FishOn.Service.PostService;
import jakarta.validation.Valid;
import com.FishOn.FishOn.Model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller REST pour la gestion des publications
 * LOMBOK UTILISÉ :
 * @RequiredArgsConstructor : Injection par constructeur automatique
 * @Slf4j : Logger automatique
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor // LOMBOK : Remplace @Autowired
@Slf4j // LOMBOK : Logger automatique
public class PostController {

    private final PostService postService;

    /**
     * Récupération du fil d'actualité global
     */
    @GetMapping("/feed")
    public List<PostResponseDTO> getFeed(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var posts = postService.getAll();

        return posts.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @GetMapping("/{userName}")
    public List<PostResponseDTO> getPostsByUserName(Authentication authentication,
                                                    @PathVariable String userName) throws UserNotFoundByUserName {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var postsUser = postService.getByUserUserName(userName);

        return postsUser.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @PostMapping
    public PostResponseDTO createPost(@Valid @RequestBody PostCreateDTO postCreateDTO, Authentication authentication)
            throws MissingTitleException, MissingDescriptionException, MissingFishNameException, MissingPhotoException, UserNotFoundById {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        // LOMBOK : Utilisation du Builder pattern
        var post = PostModel.builder()
                .title(postCreateDTO.getTitle())
                .description(postCreateDTO.getDescription())
                .fishName(postCreateDTO.getFishName())
                .photoUrl(postCreateDTO.getPhotoUrl())
                .weight(postCreateDTO.getWeight())
                .length(postCreateDTO.getLength())
                .location(postCreateDTO.getLocation())
                .catchDate(postCreateDTO.getCatchDate())
                .build();

        var createPost = postService.createPost(currentUserId, post);

        log.info("Nouvelle publication créée par {}: {}",
                userDetails.getUser().getUserName(), createPost.getTitle());

        return convertToResponseDTO(createPost);
    }

    @PutMapping("/{postId}")
    public PostResponseDTO updatePost(@PathVariable UUID postId, @Valid @RequestBody PostUpdateDTO postUpdateDTO,
                                      Authentication authentication)
            throws MissingTitleException, MissingDescriptionException, MissingFishNameException, MissingPhotoException,
            UserNotFoundById, PostNotFoundById, UnauthorizedModificationPost {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        var post = PostModel.builder()
                .title(postUpdateDTO.getTitle())
                .description(postUpdateDTO.getDescription())
                .fishName(postUpdateDTO.getFishName())
                .photoUrl(postUpdateDTO.getPhotoUrl())
                .weight(postUpdateDTO.getWeight())
                .length(postUpdateDTO.getLength())
                .location(postUpdateDTO.getLocation())
                .catchDate(postUpdateDTO.getCatchDate())
                .build();

        var updatePost = postService.updatePost(currentUserId, postId, post);

        return convertToResponseDTO(updatePost);
    }

    @DeleteMapping("/{postId}")
    public String deletePost(@PathVariable UUID postId, Authentication authentication)
            throws PostNotFoundById, UserNotFoundById, UnauthorizedModificationPost {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        postService.deletePost(currentUserId, postId);

        return "Publication supprimée";
    }

    // =============== ENDPOINTS ADMIN UNIQUEMENT ===============

    /**
     * Supprimer n'importe quelle publication (ADMIN UNIQUEMENT)
     */
    @DeleteMapping("/admin/{postId}")
    public String deletePostByAdmin(@PathVariable UUID postId, Authentication authentication)
            throws PostNotFoundById {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUser = userDetails.getUser();

        // Vérification admin
        if (!currentUser.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux administrateurs");
        }

        postService.deletePostByAdmin(postId);
        log.info("Admin {} a supprimé la publication {}", currentUser.getUserName(), postId);

        return "Publication supprimée avec succès par l'administrateur";
    }

    private PostResponseDTO convertToResponseDTO(PostModel post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .title(post.getTitle())
                .description(post.getDescription())
                .fishName(post.getFishName())
                .photoUrl(post.getPhotoUrl())
                .weight(post.getWeight())
                .length(post.getLength())
                .location(post.getLocation())
                .catchDate(post.getCatchDate())
                .userName(post.getUser().getUserName())
                .userProfilePicture(post.getUser().getProfilePicture())
                .comments(post.getComments().stream()
                        .map(comment -> CommentResponseDTO.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                                .userName(comment.getUser().getUserName())
                                .userProfilePicture(comment.getUser().getProfilePicture())
                                .build())
                        .toList())
                .build();
    }
}
