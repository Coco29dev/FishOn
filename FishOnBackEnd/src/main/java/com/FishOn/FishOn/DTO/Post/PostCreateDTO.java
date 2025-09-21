package com.FishOn.FishOn.DTO.Post;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostCreateDTO {

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

    // Constructeur vide obligatoire pour Jackson
    public PostCreateDTO() {

    }

    // Constructeur paramétré
    public PostCreateDTO(String title, String description, String fishName, String photoUrl, Double weight,
                         Double length, String location, LocalDateTime catchDate) {
        this.title = title;
        this.description = description;
        this.fishName = fishName;
        this.photoUrl = photoUrl;
        this.weight = weight;
        this.length = length;
        this.location = location;
        this.catchDate = catchDate;
    }

    // Getters
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

    public Double getWeight() {
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

    // Setters
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

    public void setWeight(Double weight) {
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
}