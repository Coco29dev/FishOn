package com.FishOn.FishOn.DTO.User;

import jakarta.validation.constraints.*;
import lombok.*;


@Data // LOMBOK : Tous les getters/setters/toString/equals/hashCode automatiques
@NoArgsConstructor // LOMBOK : Constructeur vide obligatoire pour Jackson
@AllArgsConstructor // LOMBOK : Constructeur avec paramètres
@Builder // LOMBOK : Pattern Builder
public class UpdateUserRequestDTO {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire") // Vérification champ n'est pas null, vide ou des espaces
    @Size(min = 1, max = 20, message = "Le nom d'utilisateur doit contenir entre 1 et 20 caractères") // Contrainte longueur
    private String userName;

    @NotBlank(message = "L'email est obligatoire") // Vérification champ n'est pas null, vide ou des espaces
    @Email(message = "Format d'email invalide") // Validation format email
    private String email;

    @NotBlank(message = "Prénom obligatoire")
    private String firstName;

    @NotBlank(message = "Nom obligatoire")
    private String lastName;

    @NotNull(message = "L'âge est obligatoire")
    @Min(value = 9, message = "Âge minimum 9 ans")
    @Max(value = 99, message = "Âge maximum 99 ans")
    private Integer age;

    private String profilePicture;

}