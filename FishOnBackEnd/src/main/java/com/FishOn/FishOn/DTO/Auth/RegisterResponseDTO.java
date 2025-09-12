package com.FishOn.FishOn.DTO.Auth;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

/**
 * DTO pour les réponses d'inscription
 * Contient les informations de l'utilisateur créé avec timestamp de création
 */
@Data // LOMBOK : Tous les getters/setters/toString/equals/hashCode automatiques
@NoArgsConstructor // LOMBOK : Constructeur vide obligatoire pour Jackson
@AllArgsConstructor // LOMBOK : Constructeur avec paramètres
@Builder // LOMBOK : Pattern Builder
public class RegisterResponseDTO {
    private UUID id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private Integer age;
    private String profilePicture;
    private LocalDateTime createdAt;
    private Boolean isAdmin;

}