package com.FishOn.FishOn.DTO.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {


    @NotBlank(message = "Le nom d'utilisateur est obligatoire") // Vérification champ n'est pas null, vide ou des espaces
    @Size(min = 1, max = 20, message = "Le nom d'utilisateur doit contenir entre 1 et 20 caractères") // Contrainte longueur
    private String userName;

    @NotBlank(message = "L'email est obligatoire") // Vérification champ n'est pas null, vide ou des espaces
    @Email(message = "Format d'email invalide") // Validation format email
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 5, message = "Le mot de passe doit contenir minimum 2 caractères")
    private String password;

    @NotBlank(message = "Prénom obligatoire")
    private String firstName;

    @NotBlank(message = "Nom obligatoire")
    private String lastName;

    @NotNull(message = "L'âge est obligatoire")
    @Min(value = 9, message = "Âge minimum 5 ans")
    @Max(value = 99, message = "Âge maximum 99 ans")
    private Integer age;

    private String profilePicture;

    // Constructeur vide obligatoire pour Jackson
    public RegisterRequestDTO() {

    }

    // Constructeur paramétré
    public RegisterRequestDTO(String userName, String email, String firstName, 
            String lastName, Integer age, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    // Getters
    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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

    // Setters
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
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
}