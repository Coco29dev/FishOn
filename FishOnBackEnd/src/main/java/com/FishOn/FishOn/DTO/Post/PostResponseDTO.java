package com.FishOn.FishOn.DTO.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.FishOn.FishOn.DTO.Comment.*;

public class PostResponseDTO {

    // Métadonnées
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Contenu principal
    private String title;
    private String description;
    private String fishName;
    private String photoUrl;

    // Données de pêche
    private Double weight;
    private Double length;
    private String location;
    private LocalDateTime catchDate;

    // Information utilisateur
    private String userName;

    // Liste commentaire
    private List<CommentResponseDTO> comments;

    // Constructeur vide obligatoire pour Jackson
    public PostResponseDTO() {

    }

// Constructeur paramétré
    public PostResponseDTO(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt,
                        String title, String description, String fishName, String photoUrl,
                        Double weight, Double length, String location, 
                        LocalDateTime catchDate, String userName,
            List<CommentResponseDTO> comments) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.title = title;
        this.description = description;
        this.fishName = fishName;
        this.photoUrl = photoUrl;
        this.weight = weight; // ⚠️ Attention à la typo
        this.length = length;
        this.location = location;
        this.catchDate = catchDate;
        this.userName = userName;
        this.comments = comments;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFishName() {
        return fishName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Double getWeight() {  // ⚠️ Corrigé "weight"
        return weight;
    }

    public Double getLength() {
        return length;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getCatchDate() {
        return catchDate;
    }

    public String getUserName() {
        return userName;
    }

    public List<CommentResponseDTO> getComments() {
        return comments;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFishName(String fishName) {
        this.fishName = fishName;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setWeight(Double weight) {  // ⚠️ Corrigé "weight"
        this.weight = weight;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCatchDate(LocalDateTime catchDate) {
        this.catchDate = catchDate;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setComments(List<CommentResponseDTO> comments) {
        this.comments = comments;
    }
}
