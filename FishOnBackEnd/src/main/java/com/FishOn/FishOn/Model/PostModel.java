package com.FishOn.FishOn.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA représentant une publication de pêche
 * LOMBOK UTILISÉ :
 * @Entity : Entité JPA (table posts)
 * @Getter @Setter : Génère tous les getters/setters
 * @NoArgsConstructor : Constructeur vide pour JPA
 * @AllArgsConstructor : Constructeur avec tous les paramètres
 * @ToString(exclude) : toString() sans user/comments (évite boucles infinies)
 * @EqualsAndHashCode(exclude) : equals/hashCode sans relations
 * @Builder : Pattern Builder pour création flexible
 */
@Entity // Marque cette classe comme entité JPA = créé table en base de données
@Table(name = "posts")
@Getter @Setter // LOMBOK : Remplace tous les getters/setters (26 lignes économisées)
@NoArgsConstructor // LOMBOK : Constructeur vide pour JPA
@AllArgsConstructor // LOMBOK : Constructeur avec tous les paramètres
@ToString(exclude = {"user", "comments"}) // LOMBOK : toString() sans relations (évite boucles infinies)
@EqualsAndHashCode(callSuper = true, exclude = {"user", "comments"}) // LOMBOK : equals/hashCode sans relations
@Builder // LOMBOK : Pattern Builder pour création flexible
public class PostModel extends BaseModel { // Héritage de la classe abstraite BaseModel

    // Champs obligatoires
    @Column(nullable = false) // Champ obligatoire en base
    private String title;

    @Column(length = 2000, nullable = false) // Limite de caractère, champ obligatoire
    private String description;

    @Column(nullable = false) // Champ obligatoire
    private String fishName;

    @Column(nullable = false) // Champ obligatoire
    private String photoUrl;

    // Champs optionnels de pêche
    @Column(nullable = true) // Champ optionnel
    private Double weight;

    @Column(nullable = true) // Champ optionnel
    private Double length;

    @Column(nullable = true) // Champ optionnel
    private String location;

    @Column(nullable = true) // Champ optionnel
    private LocalDateTime catchDate;

    // Relations JPA
    @ManyToOne
    @JoinColumn(name = "userId") // Clé étrangère
    @JsonIgnore // Éviter boucles lors de la sérialisation
    private UserModel user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // LOMBOK : Valeur par défaut pour le Builder
    private List<CommentModel> comments = new ArrayList<>();

    // Constructeur personnalisé pour compatibilité avec le code existant
    // Gardé car utilisé dans votre logique métier actuelle
    public PostModel(String title, String description, String fishName, String photoUrl) {
        super(); // Appel constructeur BaseModel
        this.title = title;
        this.description = description;
        this.fishName = fishName;
        this.photoUrl = photoUrl;
    }

    // NOUVEAUX AVANTAGES AVEC LOMBOK :
    // - Pattern Builder : PostModel.builder().title("test").description("test").build()
    // - toString() sans boucles infinies
    // - Maintenance automatique des getters/setters
}