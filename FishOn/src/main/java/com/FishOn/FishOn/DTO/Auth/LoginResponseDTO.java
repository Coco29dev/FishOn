package com.FishOn.FishOn.DTO.Auth;

import java.util.UUID;

public class LoginResponseDTO {

    private UUID id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private Integer age;
    private String profilePicture;

    // Constructeur vide pour Jackson
    public LoginResponseDTO() {
    }

    // Constructeur paramétré
    public LoginResponseDTO(UUID id, String userName, String email,
            String firstName, String lastName, Integer age,
            String profilePicture) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.profilePicture = profilePicture;
    }

    // Getters
    public UUID getId() {
        return id;
    }

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

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

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
