package com.FishOn.FishOn.DTO.Post;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data // LOMBOK : Tous les getters/setters/toString/equals/hashCode automatiques
@NoArgsConstructor // LOMBOK : Constructeur vide obligatoire pour Jackson
@AllArgsConstructor // LOMBOK : Constructeur avec paramètres
@Builder // LOMBOK : Pattern Builder
public class PostUpdateDTO {

        // Obligatoire
    @NotBlank(message = "Titre obligatoire")
    @Size(max = 50, message = "Titre dépassant la limite de caractère")
    private String title;

    @NotBlank(message = "Description obligatoire")
    @Size(max = 2000, message = "Description dépassant la limite de caractère")
    private String description;

    @NotBlank(message = "Nom de poisson obligatoire")
    @Size(max = 20, message = "Nom de poisson dépassant la limite de caractère")
    private String fishName;

    @NotBlank(message = "Photo obligatoire")
    private String photoUrl;

    // Optionnels
    private Double weight;
    private Double length;
    private String location;
    private LocalDateTime catchDate;

}