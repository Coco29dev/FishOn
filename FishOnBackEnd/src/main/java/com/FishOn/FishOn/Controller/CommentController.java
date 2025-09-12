package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Service.CommentService;
import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.DTO.Comment.*;
import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Exception.FishOnException.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion des commentaires
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    /**
     * Création d'un commentaire sur un post
     */
    @PostMapping("/post/{postId}")
    @PreAuthorize("isAuthenticated()")
    public CommentResponseDTO createComment(@PathVariable UUID postId, @Valid @RequestBody CommentCreateDTO commentCreateDTO,
                                            Authentication authentication)
            throws UserNotFoundById, PostNotFoundById {

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        var comment = CommentModel.builder()
                .content(commentCreateDTO.getContent())
                .build();

        var savedComment = commentService.createComment(comment, currentUserId, postId);

        log.info("Nouveau commentaire créé par {}: {}",
                userDetails.getUser().getUserName(), savedComment.getContent());

        return convertToResponseDTO(savedComment);
    }

    /**
     * Modification d'un commentaire
     */
    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public CommentResponseDTO updateComment(@PathVariable UUID commentId, @Valid @RequestBody CommentUpdateDTO commentUpdateDTO,
                                            Authentication authentication)
            throws CommentNotFound, UnauthorizedAccess {

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        var updatedComment = CommentModel.builder()
                .content(commentUpdateDTO.getContent())
                .build();

        var savedComment = commentService.updateComment(commentId, updatedComment, currentUserId);

        return convertToResponseDTO(savedComment);
    }

    /**
     * Suppression d'un commentaire
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable UUID commentId, Authentication authentication)
            throws CommentNotFound, UnauthorizedAccess {

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        commentService.deleteComment(commentId, currentUserId);

        return "Commentaire supprimé avec succès";
    }

    /**
     * Récupération des commentaires d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public List<CommentResponseDTO> getCommentsByUserId(@PathVariable UUID userId, Authentication authentication)
            throws UserNotFoundById {

        var comments = commentService.getByUserId(userId);

        return comments.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Récupération des commentaires d'un post
     */
    @GetMapping("/post/{postId}")
    @PreAuthorize("isAuthenticated()")
    public List<CommentResponseDTO> getCommentsByPostId(@PathVariable UUID postId, Authentication authentication)
            throws PostNotFoundById {

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
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCommentByAdmin(@PathVariable UUID commentId, Authentication authentication)
            throws CommentNotFound {

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUser = userDetails.getUser();

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