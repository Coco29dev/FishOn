package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Service.CommentService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.DTO.Comment.*;
import com.FishOn.FishOn.Model.CommentModel;

import com.FishOn.FishOn.Exception.FishOnException.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;


    // Méthode CRUD
    @PostMapping("/post/{postId}")
    public CommentResponseDTO createComment(@PathVariable UUID postId, @Valid @RequestBody CommentCreateDTO commentCreateDTO, Authentication authentication)
    throws UserNotFoundById, PostNotFoundById {

        // Vérification de sécurité identique aux autres controllers
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération de l'ID utilisateur depuis l'authentification
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUser().getId();

        CommentModel comment = new CommentModel(commentCreateDTO.getContent());

        CommentModel savedComment = commentService.createComment(comment,  currentUserId, postId);

        return new CommentResponseDTO(
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getCreatedAt(),
                savedComment.getUpdatedAt(),
                savedComment.getUser().getUserName(),
                savedComment.getUser().getProfilePicture()
        );
    }


    @PutMapping("/{commentId}")
    public CommentResponseDTO updateComment(@PathVariable UUID commentId, @Valid @RequestBody CommentUpdateDTO commentUpdateDTO, Authentication authentication)
            throws CommentNotFound, UnauthorizedAccess {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération de l'ID utilisateur depuis l'authentification
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUser().getId();

        CommentModel updatedComment = new CommentModel(commentUpdateDTO.getContent());

        CommentModel savedComment = commentService.updateComment(commentId, updatedComment, currentUserId);

        return new CommentResponseDTO(
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getCreatedAt(),
                savedComment.getUpdatedAt(),
                savedComment.getUser().getUserName(),
                savedComment.getUser().getProfilePicture()
        );
    }

    @DeleteMapping("/{commentId}")
    public String deleteComment(@PathVariable UUID commentId, Authentication authentication) throws CommentNotFound, UnauthorizedAccess {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération de l'ID utilisateur depuis l'authentification
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUser().getId();

        commentService.deleteComment(commentId, currentUserId);

        return "Commentaire supprimé avec succès";
    }

    // Méthode recherche
    @GetMapping("/user/{userId}")
    public List<CommentResponseDTO> getCommentsByUserId(@PathVariable UUID userId, Authentication authentication) throws UserNotFoundById {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        List<CommentModel> comments = commentService.getByUserId(userId);

        return comments.stream()
                .map(comment -> new CommentResponseDTO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt(),
                        comment.getUser().getUserName(),
                        comment.getUser().getProfilePicture()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/post/{postId}")
    public List<CommentResponseDTO> getCommentsByPostId(@PathVariable UUID postId, Authentication authentication) throws PostNotFoundById {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        List<CommentModel> comments = commentService.getByPostId(postId);

        return comments.stream()
                .map(comment -> new CommentResponseDTO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt(),
                        comment.getUser().getUserName(),
                        comment.getUser().getProfilePicture()
                ))
                .collect(Collectors.toList());
    }
}