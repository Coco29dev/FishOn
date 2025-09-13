package com.FishOn.FishOn.DTO.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.FishOn.FishOn.Model.PostModel;
import lombok.*;
import com.FishOn.FishOn.DTO.Comment.*;

@Data // LOMBOK : Tous les getters/setters/toString/equals/hashCode automatiques
@NoArgsConstructor // LOMBOK : Constructeur vide obligatoire pour Jackson
@AllArgsConstructor // LOMBOK : Constructeur avec paramètres
@Builder // LOMBOK : Pattern Builder
public class PostResponseDTO {

    // Métadonnées
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Contenu principal
    private String title;
    private String description;
    private String fishName;
    private String photoUrl;

    // Données de pêche
    private Double weight;
    private Double length;
    private String location;
    private LocalDateTime catchDate;

    // Information utilisateur
    private String userName;
    private String userProfilePicture;

    // Liste commentaire
    private List<CommentResponseDTO> comments;

    /**
     * Conversion depuis PostModel vers PostResponseDTO
     */
    public static PostResponseDTO from(PostModel post) {
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
                        .map(CommentResponseDTO::from)
                        .toList())
                .build();
    }
}
