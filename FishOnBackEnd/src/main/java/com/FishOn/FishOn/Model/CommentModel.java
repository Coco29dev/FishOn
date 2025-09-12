package com.FishOn.FishOn.Model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité JPA représentant un commentaire sur une publication
 *
 * LOMBOK UTILISÉ :
 * @Entity : Entité JPA (table comments)
 * @Getter @Setter : Génère tous les getters/setters
 * @NoArgsConstructor : Constructeur vide pour JPA
 * @AllArgsConstructor : Constructeur avec tous les paramètres
 * @ToString(exclude) : toString() sans user/post (évite boucles infinies)
 * @EqualsAndHashCode(exclude) : equals/hashCode sans relations
 * @Builder : Pattern Builder pour création flexible
 */
@Entity // Marque cette classe comme entité JPA = créé table en base de données
@Table(name = "comments")
@Getter @Setter // LOMBOK : Remplace tous les getters/setters (8 lignes économisées)
@NoArgsConstructor // LOMBOK : Constructeur vide pour JPA
@AllArgsConstructor // LOMBOK : Constructeur avec tous les paramètres
@ToString(exclude = {"user", "post"}) // LOMBOK : toString() sans relations (évite boucles infinies)
@EqualsAndHashCode(callSuper = true, exclude = {"user", "post"}) // LOMBOK : equals/hashCode sans relations
@Builder // LOMBOK : Pattern Builder pour création flexible
public class CommentModel extends BaseModel { // Héritage de la classe abstraite BaseModel

    @Column(length = 1000, nullable = false) // Limite de caractère, champ obligatoire
    private String content;

    @ManyToOne
    @JoinColumn(name  = "postId") // Clé étrangère
    private PostModel post;

    @ManyToOne
    @JoinColumn(name = "userId") // Clé étrangère
    private UserModel user;

    // Constructeur personnalisé pour compatibilité avec le code existant
    // Gardé car utilisé dans votre logique métier actuelle
    public CommentModel(String content) {
        super(); // Appel constructeur BaseModel
        this.content = content;
    }

    // NOUVEAUX AVANTAGES AVEC LOMBOK :
    // - Pattern Builder : CommentModel.builder().content("Super prise !").build()
    // - toString() sans boucles infinies
    // - Maintenance automatique
}