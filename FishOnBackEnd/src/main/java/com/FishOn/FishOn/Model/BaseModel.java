package com.FishOn.FishOn.Model; // Défintion espace noms de projet, organisation arborescence projet

import jakarta.persistence.*; // Importation annotations JPA

import java.util.*;

import java.time.LocalDateTime;// Importation classe utilitaires (UUID)

// Importation annotations spécifique hibernate pour gestion automatique
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass // Marque cette classe comme super classe pour JPA
public abstract class BaseModel {

    @Id // Marque attribut comme clé primaire
    @GeneratedValue(strategy = GenerationType.UUID) // Génération automatique uuid
    private UUID id;

    @CreationTimestamp // Hibernate rempli automatiquement ce champ à la création
    private LocalDateTime createdAt;

    @UpdateTimestamp // Hibernate MAJ automatiquement ce champ à chaque modification
    private LocalDateTime updatedAt;

    // Constructeur vide obligatoire pour JPA
    public BaseModel() {

    }

    // Constructeur paramétré
    public BaseModel(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
}