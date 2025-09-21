package com.FishOn.FishOn.DTO.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentResponseDTO {

    private UUID id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userName;
    private String userProfilePicture;


    // Constructeur par défaut
    public CommentResponseDTO() {}

    // Constructeur avec paramètres
    public CommentResponseDTO(UUID id, String content, LocalDateTime createdAt, LocalDateTime updatedAt, String userName,  String userProfilePicture) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userName = userName;
        this.userProfilePicture = userProfilePicture;

    }

    // Getter
    public UUID getId() {
        return id;
    }
    public String getContent() {
        return content;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserProfilePicture() { return userProfilePicture; }


    // Setter
    public void setId(UUID id) {
        this.id = id;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUserProfilePicture(String userProfilePicture) { this.userProfilePicture = userProfilePicture; }

}