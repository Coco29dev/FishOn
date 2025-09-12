package com.FishOn.FishOn.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe de base abstraite pour tous les modèles
 * Fournit les champs communs : id, createdAt, updatedAt
 * LOMBOK UTILISÉ :
 * @Getter @Setter : Génère automatiquement tous les getters/setters
 * @NoArgsConstructor : Génère constructeur vide obligatoire pour JPA
 * @AllArgsConstructor : Génère constructeur avec tous les paramètres
 * @ToString : Génère méthode toString() automatique
 * @EqualsAndHashCode : Génère equals() et hashCode() automatiques
 */
@MappedSuperclass // Marque cette classe comme super classe pour JPA
@Getter @Setter // LOMBOK : Génère automatiquement tous les getters et setters
@NoArgsConstructor // LOMBOK : Génère constructeur vide public BaseModel() {}
@AllArgsConstructor // LOMBOK : Génère constructeur avec tous les paramètres
@ToString // LOMBOK : Génère toString() automatique
@EqualsAndHashCode // LOMBOK : Génère equals() et hashCode() automatiques
public abstract class BaseModel {

    @Id // Marque attribut comme clé primaire
    @GeneratedValue(strategy = GenerationType.UUID) // Génération automatique uuid
    private UUID id;

    @CreationTimestamp // Hibernate rempli automatiquement ce champ à la création
    private LocalDateTime createdAt;

    @UpdateTimestamp // Hibernate MAJ automatiquement ce champ à chaque modification
    private LocalDateTime updatedAt;

}
