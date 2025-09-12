package com.FishOn.FishOn.DTO.Comment;

import jakarta.validation.constraints.*;
import lombok.*;

@Data // LOMBOK : Tous les getters/setters/toString/equals/hashCode automatiques
@NoArgsConstructor // LOMBOK : Constructeur vide obligatoire pour Jackson
@AllArgsConstructor // LOMBOK : Constructeur avec paramètres
@Builder // LOMBOK : Pattern Builder
public class CommentUpdateDTO {

    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    @Size(max = 1000, message = "Le commentaire ne peut pas dépasser 1000 caractères")
    private String content;

}