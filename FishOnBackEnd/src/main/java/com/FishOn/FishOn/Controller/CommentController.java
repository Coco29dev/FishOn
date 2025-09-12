package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Service.CommentService;
import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.DTO.Comment.*;
import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Exception.FishOnException.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion des commentaires
 * LOMBOK UTILISÉ :
 * @RequiredArgsConstructor : Injection par constructeur automatique
 * @Slf4j : Logger automatique
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor // LOMBOK : Remplace @Autowired
@Slf4j // LOMBOK : Logger automatique
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    public CommentResponseDTO createComment(@PathVariable UUID postId, @Valid @RequestBody CommentCreateDTO commentCreateDTO,
                                            Authentication authentication)
            throws UserNotFoundById, PostNotFoundById {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        // LOMBOK : Utilisation du Builder pattern
        var comment = CommentModel.builder()
                .content(commentCreateDTO.getContent())
                .build();

        var savedComment = commentService.createComment(comment, currentUserId, postId);

        log.info("Nouveau commentaire créé par {}: {}",
                userDetails.getUser().getUserName(), savedComment.getContent());

        return convertToResponseDTO(savedComment);
    }

    @PutMapping("/{commentId}")
    public CommentResponseDTO updateComment(@PathVariable UUID commentId, @Valid @RequestBody CommentUpdateDTO commentUpdateDTO,
                                            Authentication authentication)
            throws CommentNotFound, UnauthorizedAccess {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        var updatedComment = CommentModel.builder()
                .content(commentUpdateDTO.getContent())
                .build();

        var savedComment = commentService.updateComment(commentId, updatedComment, currentUserId);

        return convertToResponseDTO(savedComment);
    }

    @DeleteMapping("/{commentId}")
    public String deleteComment(@PathVariable UUID commentId, Authentication authentication)
            throws CommentNotFound, UnauthorizedAccess {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        commentService.deleteComment(commentId, currentUserId);

        return "Commentaire supprimé avec succès";
    }

    @GetMapping("/user/{userId}")
    public List<CommentResponseDTO> getCommentsByUserId(@PathVariable UUID userId, Authentication authentication)
            throws UserNotFoundById {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var comments = commentService.getByUserId(userId);

        return comments.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @GetMapping("/post/{postId}")
    public List<CommentResponseDTO> getCommentsByPostId(@PathVariable UUID postId, Authentication authentication)
            throws PostNotFoundById {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var comments = commentService.getByPostId(postId);

        return comments.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // =============== ENDPOINTS ADMIN UNIQUEMENT ===============

    /**
     * Supprimer n'importe quel commentaire (ADMIN UNIQUEMENT)
     */
    @DeleteMapping("/admin/{commentId}")
    public String deleteCommentByAdmin(@PathVariable UUID commentId, Authentication authentication)
            throws CommentNotFound {

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

        commentService.deleteCommentByAdmin(commentId);
        log.info("Admin {} a supprimé le commentaire {}", currentUser.getUserName(), commentId);

        return "Commentaire supprimé avec succès par l'administrateur";
    }

    private CommentResponseDTO convertToResponseDTO(CommentModel comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .userName(comment.getUser().getUserName())
                .userProfilePicture(comment.getUser().getProfilePicture())
                .build();
    }
}