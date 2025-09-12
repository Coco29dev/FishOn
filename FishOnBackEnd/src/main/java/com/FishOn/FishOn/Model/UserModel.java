package com.FishOn.FishOn.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA représentant un utilisateur de FishOn
 *
 * LOMBOK UTILISÉ :
 * @Entity : Entité JPA (table users)
 * @Getter @Setter : Génère tous les getters/setters automatiquement
 * @NoArgsConstructor : Constructeur vide pour JPA
 * @AllArgsConstructor : Constructeur avec tous les paramètres
 * @ToString(exclude) : toString() en excluant les champs sensibles/collections
 * @EqualsAndHashCode(exclude) : equals/hashCode en excluant collections (évite boucles infinies)
 * @Builder : Pattern Builder pour construction flexible
 */
@Entity // Création d'un Model (entity) User
@Table(name = "users") // Création d'une table users
@Getter @Setter // LOMBOK : Remplace tous les getters/setters (24 lignes économisées)
@NoArgsConstructor // LOMBOK : Constructeur vide pour JPA
@AllArgsConstructor // LOMBOK : Constructeur avec tous les paramètres
@ToString(exclude = {"password", "posts", "comments"}) // LOMBOK : toString() sans données sensibles/collections
@EqualsAndHashCode(callSuper = true, exclude = {"posts", "comments"}) // LOMBOK : equals/hashCode sans collections
@Builder // LOMBOK : Pattern Builder pour création flexible
public class UserModel extends BaseModel { // Héritage de BaseModel pour id, createdAt, updatedAt

    // Attribut unique et non null
    @Column(unique = true, nullable = false)
    private String userName;

    // Attribut unique, non null et format adresse email
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
    @JsonIgnore // Éviter l'inclusion du mot de passe dans la sérialisation JSON
    private String password;

    @Column(nullable = true)
    private String profilePicture;

    @Column(nullable = false)
    @Builder.Default // Valeur par défaut avec Lombok Builder
    private Boolean isAdmin = false; // Par défaut : utilisateur normal

    // Relation one to many avec Post, relation cascade
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // LOMBOK : Valeur par défaut pour le Builder
    private List<PostModel> posts = new ArrayList<>();

    // Relation one to many avec Comment, relation cascade
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // LOMBOK : Valeur par défaut pour le Builder
    private List<CommentModel> comments = new ArrayList<>();

    // Constructeur personnalisé pour compatibilité avec le code existant
    // Ce constructeur reste nécessaire car utilisé dans votre code actuel
    public UserModel(String userName, String email, String firstName, String lastName, Integer age, String password, String profilePicture) {
        super(); // Appel constructeur BaseModel
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.password = password;
        this.profilePicture = profilePicture;
        this.isAdmin = false;
    }
    /**
     * Vérifie si l'utilisateur est administrateur
     * @return true si admin, false sinon
     */
    public boolean isAdmin() {
        return Boolean.TRUE.equals(isAdmin);
    }

    /**
     * Vérifie si l'utilisateur est un utilisateur normal
     * @return true si utilisateur normal, false si admin
     */
    public boolean isUser() {
        return !isAdmin();
    }
    // NOUVEAUX AVANTAGES AVEC LOMBOK :
    // - Pattern Builder : UserModel.builder().userName("test").email("test@test.com").build()
    // - toString() automatique sans révéler le mot de passe
    // - equals/hashCode sans risque de boucle infinie avec les collections
}