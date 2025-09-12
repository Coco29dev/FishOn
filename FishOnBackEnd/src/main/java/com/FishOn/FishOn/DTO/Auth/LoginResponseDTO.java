package com.FishOn.FishOn.DTO.Auth;

import lombok.*;
import java.util.UUID;

/**
 * DTO pour les réponses de connexion
 * Contient les informations de l'utilisateur connecté (sans mot de passe)
 */
@Data // LOMBOK : Remplace tous les getters/setters/toString/equals/hashCode
@NoArgsConstructor // LOMBOK : Constructeur vide pour Jackson
@AllArgsConstructor // LOMBOK : Constructeur avec paramètres
@Builder // LOMBOK : Pattern Builder
public class LoginResponseDTO {
    private UUID id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private Integer age;
    private String profilePicture;
    private Boolean isAdmin;

}