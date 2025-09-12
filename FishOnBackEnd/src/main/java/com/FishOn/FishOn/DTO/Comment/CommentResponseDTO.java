package com.FishOn.FishOn.DTO.Comment;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Data // LOMBOK : @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@NoArgsConstructor // LOMBOK : Constructeur vide obligatoire pour Jackson
@AllArgsConstructor // LOMBOK : Constructeur avec paramètres
@Builder // LOMBOK : Pattern Builder
public class CommentResponseDTO {

    private UUID id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userName;
    private String userProfilePicture;

}