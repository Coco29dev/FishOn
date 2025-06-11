package com.FishOn.FishOn.Model; // Défintion espace noms de projet, organisation arborescence projet

import java.time.LocalDateTime; // Importation classe utilitaires (UUID)

import com.fasterxml.jackson.annotation.JsonIgnore; // Éviter boucle infines lors de la sérialisation JSON

// Importation annotations JPA
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;

import java.util.*;

import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Model.CommentModel;

@Entity // Marque cette classe comme entité JPA = créé table en base de données
@Table(name = "posts")
// Héritage de la classe abstraite BaseModel
public class PostModel extends BaseModel {
    // Attribut
    @Column(nullable = false) // Champ obligatoire en base
    private String title;

    @Column(length = 2000, nullable = false) // Limite de caractère, champ obligatoire
    private String description;

    @Column(nullable = false) // Champ obligatoire
    private String fishName;

    @Column(nullable = true) // Champ optionnel
    private Double weight;

    @Column(nullable = true) // Champ optionnel
    private Double length;

    @Column(nullable = true) // Champ optionnel
    private String location;

    @Column(nullable = true) // Champ optionnel
    private LocalDateTime catchDate;

    @ManyToOne
    @JoinColumn(name = "userId") // Clé étrangère
    @JsonIgnore // Éviter boucles lors de la sérialisation
    private UserModel user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentModel> comments = new ArrayList<>();


    // Constructeur vide obligatoire pour JPA
    public PostModel() {
    }

    // Construteur paramétré
    public PostModel(String title, String description, String fishName) {
        super(); // Appel constructeur BaseModel
        this.title = title;
        this.description = description;
        this.fishName = fishName;
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

    public UserModel getUser() {
        return user;
    }

    public List<CommentModel> getComments() {
        return comments;
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

    public void setUser(UserModel user) {
        this.user = user;
    }

    public void setComments(List<CommentModel> comments) {
        this.comments = comments;
    }
}