package com.FishOn.FishOn.DTO.User;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;


@Data // LOMBOK : Tous les getters/setters/toString/equals/hashCode automatiques
@NoArgsConstructor // LOMBOK : Constructeur vide obligatoire pour Jackson
@AllArgsConstructor // LOMBOK : Constructeur avec paramètres
@Builder // LOMBOK : Pattern Builder
public class UpdateUserResponseDTO {
    private UUID id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private Integer age;
    private String profilePicture;
    private LocalDateTime updatedAt;
    private Boolean isAdmin;
    
}