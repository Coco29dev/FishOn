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
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pour la gestion des publications
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    /**
     * Récupération du fil d'actualité global
     */
    @GetMapping("/feed")
    @PreAuthorize("isAuthenticated()")
    public List<PostResponseDTO> getFeed(Authentication authentication) {
        var posts = postService.getAll();

        return posts.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Publications d'un utilisateur spécifique
     */
    @GetMapping("/{userName}")
    @PreAuthorize("isAuthenticated()")
    public List<PostResponseDTO> getPostsByUserName(Authentication authentication,
                                                    @PathVariable String userName) throws UserNotFoundByUserName {

        var postsUser = postService.getByUserUserName(userName);

        return postsUser.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Création d'une nouvelle publication
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public PostResponseDTO createPost(@Valid @RequestBody PostCreateDTO postCreateDTO, Authentication authentication)
            throws MissingTitleException, MissingDescriptionException, MissingFishNameException, MissingPhotoException, UserNotFoundById {

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

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

    /**
     * Modification d'une publication
     */
    @PutMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public PostResponseDTO updatePost(@PathVariable UUID postId, @Valid @RequestBody PostUpdateDTO postUpdateDTO,
                                      Authentication authentication)
            throws MissingTitleException, MissingDescriptionException, MissingFishNameException, MissingPhotoException,
            UserNotFoundById, PostNotFoundById, UnauthorizedModificationPost {

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

    /**
     * Suppression d'une publication
     */
    @DeleteMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public String deletePost(@PathVariable UUID postId, Authentication authentication)
            throws PostNotFoundById, UserNotFoundById, UnauthorizedModificationPost {

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
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePostByAdmin(@PathVariable UUID postId, Authentication authentication)
            throws PostNotFoundById {

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUser = userDetails.getUser();

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