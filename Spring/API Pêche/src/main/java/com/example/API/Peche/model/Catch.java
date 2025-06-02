package com.example.API.Peche.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

@Entity
@Table(name = "catches")
public class Catch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fishName;
    
    @Column
    private Double weight;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private LocalDateTime catchDate;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructeur vide (requis par JPA)
    public Catch() {}
    
    // Constructeur avec paramètres
    public Catch(String fishName, Double weight, String location, User user) {
        this.fishName = fishName;
        this.weight = weight;
        this.location = location;
        this.user = user;
        this.catchDate = LocalDateTime.now();
    }
    
    // Getters
    public Long getId() {
        return id;
    }

    public String getFishName() {
        return fishName;
    }

    public Double getWeight() {
        return weight;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getCatchDate() {
        return catchDate;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setFishName(String fishName) {
        this.fishName = fishName;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCatchDate(LocalDateTime catchDate) {
        this.catchDate = catchDate;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
