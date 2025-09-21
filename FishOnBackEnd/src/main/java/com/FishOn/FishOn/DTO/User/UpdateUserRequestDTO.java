package com.FishOn.FishOn.DTO.User;

import jakarta.validation.constraints.*;


public class UpdateUserRequestDTO {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire") // Vérification champ n'est pas null, vide ou des espaces
    @Size(min = 1, max = 20, message = "Le nom d'utilisateur doit contenir entre 1 et 20 caractères") // Contrainte longueur
    private String userName;

    @NotBlank(message = "L'email est obligatoire") // Vérification champ n'est pas null, vide ou des espaces
    @Email(message = "Format d'email invalide") // Validation format email
    private String email;

    @NotBlank(message = "Prénom obligatoire")
    private String firstName;

    @NotBlank(message = "Nom obligatoire")
    private String lastName;

    @NotNull(message = "L'âge est obligatoire")
    @Min(value = 9, message = "Âge minimum 9 ans")
    @Max(value = 99, message = "Âge maximum 99 ans")
    private Integer age;

    private String profilePicture;

    // Constructeur par défaut
    public UpdateUserRequestDTO() {}

    // Constructeur avec paramètres
    public UpdateUserRequestDTO(String userName, String email, String firstName, String lastName, Integer age, String profilePicture) {
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.profilePicture = profilePicture;

    }

    // Getter
    public String getUserName() {
        return userName;
    }
    public String getEmail() {
        return email;
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
    public String getProfilePicture() {
        return profilePicture;
    }

    // Setter
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setEmail(String email) {
        this.email = email;
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
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}