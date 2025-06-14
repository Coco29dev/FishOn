package com.FishOn.FishOn.DTO.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentResponseDTO {

    private UUID id;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userName;
    private String postTitle;

    // Constructeur par défaut
    public CommentResponseDTO() {}

    // Constructeur avec paramètres
    public CommentResponseDTO(UUID id, String comment, LocalDateTime createdAt, LocalDateTime updatedAt, String userName, String postTitle  ) {
        this.id = id;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userName = userName;
        this.postTitle = postTitle;
    }

    // Getter
    public UUID getId() {
        return id;
    }
    public String getComment() {
        return comment;
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
    public String getPostTitle() {
        return postTitle;
    }

    // Setter
    public void setId(UUID id) {
        this.id = id;
    }
    public void setComment(String comment) {
        this.comment = comment;
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
    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

}