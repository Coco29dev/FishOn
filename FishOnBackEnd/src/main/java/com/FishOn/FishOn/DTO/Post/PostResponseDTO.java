package com.FishOn.FishOn.DTO.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

}
