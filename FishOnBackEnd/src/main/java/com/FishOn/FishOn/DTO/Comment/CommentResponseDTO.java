package com.FishOn.FishOn.DTO.Comment;


import java.time.LocalDateTime;
import java.util.UUID;

import com.FishOn.FishOn.Model.CommentModel;
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

    /**
     * Conversion depuis CommentModel vers CommentResponseDTO
     */
    public static CommentResponseDTO from(CommentModel comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .userName(comment.getUser().getUserName())
                .userProfilePicture(comment.getUser().getProfilePicture())
                .build();
    }
}