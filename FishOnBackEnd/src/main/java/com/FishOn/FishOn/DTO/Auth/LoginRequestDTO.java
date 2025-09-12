package com.FishOn.FishOn.DTO.Auth;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO pour les requêtes de connexion
 * LOMBOK UTILISÉ :
 * @Data : Combinaison de @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
 * @NoArgsConstructor : Constructeur vide obligatoire pour Jackson (désérialisation JSON)
 * @AllArgsConstructor : Constructeur avec tous les paramètres
 * @Builder : Pattern Builder pour tests et création flexible
 */
@Data // LOMBOK : @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@NoArgsConstructor // LOMBOK : Constructeur vide obligatoire pour Jackson
@AllArgsConstructor // LOMBOK : Constructeur avec paramètres
@Builder // LOMBOK : Pattern Builder
public class LoginRequestDTO {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Mot de passe est obligatoire")
    @Size(min = 5, message = "Le mot de passe doit contenir minimum 5 caractères")
    private String password;

    // NOUVEAUX AVANTAGES :
    // - Création : LoginRequestDTO.builder().email("test@test.com").password("pass").build()
    // - Tous les getters/setters disponibles automatiquement
    // - toString() automatique pour debug
}