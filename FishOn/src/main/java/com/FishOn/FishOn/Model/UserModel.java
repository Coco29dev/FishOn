package com.FishOn.FishOn.Model;

import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.CommentModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;


import java.util.ArrayList;
import java.util.List;


@Entity // Création d'un Model (entity) User
@Table(name = "users") // Création d'une table users
public class UserModel extends BaseModel { // Création d'une classe UserModel qui hérite de BaseModel

    // Attribut unique et non null
    @Column(unique = true, nullable = false)
    private String userName;

    // Attriut unique, non null et format adresse email
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = true)
    private String profilePicture;

    // Relation one to many avec List, relation cascade
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostModel> posts = new ArrayList<>();

    // Relation one to many avec Comment, relation cascade
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentModel> comments = new ArrayList<>();

    // Constructeur par défaut
    public UserModel() {}

    // Constructeur avec paramètre + héritage des attributs de BaseModel avec super()
    public UserModel(String userName, String email, String firstName, String lastName, Integer age, String password, boolean enabled, String profilePicture) {
        super();
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.password = password;
        this.enabled = enabled;
        this.profilePicture = profilePicture;
    }

    //Getter

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() {
        return age;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public List<PostModel> getPosts() {
        return posts;
    }

    public List<CommentModel> getComments() {
        return comments;
    }

    // Setter

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setPosts(List<PostModel> posts) {
        this.posts = posts;
    }

    public void setComments(List<CommentModel> comments) {
        this.comments = comments;
    }
}