package com.FishOn.FishOn.DTO.Auth;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO pour les requêtes d'inscription
 * Contient toutes les données nécessaires pour créer un nouvel utilisateur
 */
@Data // LOMBOK : Génère automatiquement getters/setters/toString/equals/hashCode
@NoArgsConstructor // LOMBOK : Constructeur vide obligatoire pour Jackson
@AllArgsConstructor // LOMBOK : Constructeur avec tous les paramètres
@Builder // LOMBOK : Pattern Builder pour tests et usage flexible
public class RegisterRequestDTO {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire") // Vérification champ n'est pas null, vide ou des espaces
    @Size(min = 1, max = 20, message = "Le nom d'utilisateur doit contenir entre 1 et 20 caractères") // Contrainte longueur
    private String userName;

    @NotBlank(message = "L'email est obligatoire") // Vérification champ n'est pas null, vide ou des espaces
    @Email(message = "Format d'email invalide") // Validation format email
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 5, message = "Le mot de passe doit contenir minimum 2 caractères")
    private String password;

    @NotBlank(message = "Prénom obligatoire")
    private String firstName;

    @NotBlank(message = "Nom obligatoire")
    private String lastName;

    @NotNull(message = "L'âge est obligatoire")
    @Min(value = 9, message = "Âge minimum 9 ans") // Validation valeurs numérique minimum
    @Max(value = 99, message = "Âge maximum 99 ans") // Validation valeurs numérique maximum
    private Integer age;

    private String profilePicture;

    // Constructeur personnalisé pour compatibilité avec les tests existants
    public RegisterRequestDTO(String userName, String email, String firstName,
                              String lastName, Integer age, String password) {
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.password = password;
    }

}