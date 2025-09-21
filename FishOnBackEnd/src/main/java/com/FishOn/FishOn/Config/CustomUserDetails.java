package com.FishOn.FishOn.Config;

import com.FishOn.FishOn.Model.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Classe pont entre UserModel et ce que Spring Security attend UserDetails
// Classe implémente interface UserDetails ce qui permet l'override avec méthode UserModel
public class CustomUserDetails implements UserDetails {

    private final UserModel user;

    public CustomUserDetails(UserModel user) {
        this.user = user;
    }

    // Getter pour accéder au UserModel complet
    public UserModel getUser() {
        return user;
    }

    @Override
    // indique à Spring Security identifiant unique est l'email
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    // Fournit mdp hashé pour authentification
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // Pas d'autorités pour l'instant
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
