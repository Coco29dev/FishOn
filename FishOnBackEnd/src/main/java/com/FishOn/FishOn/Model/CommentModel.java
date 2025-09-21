package com.FishOn.FishOn.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;

@Entity // Marque cette classe comme entité JPA = créé table en base de données
@Table(name = "comments")
// Héritage de la classe abstraite BaseModel
public class CommentModel extends BaseModel {

    @Column(length = 1000, nullable = false) // Limite de caractère, champ obligatoire
    private String content;

    @ManyToOne
    @JoinColumn(name  = "postId") // Clé étrangère
    private PostModel post;

    @ManyToOne
    @JoinColumn(name = "userId") // Clé étrangère
    private UserModel user;

    // Constructeur vide obligatoire pour JPA
    public CommentModel() {

    }

    // Constructeur paramétré
    public CommentModel(String content) {
        super(); // Appel constructeur BaseModel
        this.content = content;
    }

    // Getters
    public String getContent() {
        return content;
    }

    public PostModel getPost() {
        return post;
    }

    public UserModel getUser() {
        return user;
    }

    // Setters
    public void setContent(String content) {
        this.content = content;
    }

    public void setPost(PostModel post) {
        this.post = post;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}