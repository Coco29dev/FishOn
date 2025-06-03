package com.example.API.Peche.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import java.util.List;
import java.util.ArrayList;

@Entity // Dit à JPA cette classe est une table en base
@Table(name = "users") // Nommage table
public class User {

    @Id // Champ clé primaire
    // PostgreSQL génère automatiquement la valeur
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Long = permet que la valeur soit null avant sauvegarde

    // Username unique et obligatoire
    @Column(unique = true, nullable = false)
    private String username;

    // Password obligatoire
    @Column(nullable = false)
    private String password;

    // Email obligatoire
    @Column(nullable = false)
    private String email;

    // User a plusieurs Catch
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    // mappedBy = Relation géré par le champ "user" dans la classe Catch
    // CascadeType.ALL = Si User supprimé Catch supprimé aussi
    private List<Catch> catches = new ArrayList<>();

    // Constructeur vide(requis pou JPA pour créer les objets)
    public User() {
    }

    // Constructeur parmétré(Faciliter création d'objets)
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public List<Catch> getCatches() {
        return catches;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCatches(List<Catch> catches) {
        this.catches = catches;
    }
}
