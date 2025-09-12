package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Service.AuthService;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.DTO.Auth.*;
import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.Exception.FishOnException.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion de l'authentification
 * MODIFICATION MINIMALE : Ajout du champ isAdmin dans les réponses
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    /**
     * Inscription d'un nouvel utilisateur
     * MODIFICATION : Ajout isAdmin dans la réponse (toujours false)
     */
    @PostMapping("/register")
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO registerRequest)
            throws EmailAlreadyExists, UserAlreadyExists {

        val user = UserModel.builder()
                .userName(registerRequest.getUserName())
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .age(registerRequest.getAge())
                .password(registerRequest.getPassword())
                .profilePicture(registerRequest.getProfilePicture())
                // isAdmin = false par défaut (Builder.Default)
                .build();

        val newUser = authService.register(user);
        log.info("Nouvel utilisateur inscrit: {} (Admin: {})", newUser.getUserName(), newUser.isAdmin());

        return RegisterResponseDTO.builder()
                .id(newUser.getId())
                .userName(newUser.getUserName())
                .email(newUser.getEmail())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .age(newUser.getAge())
                .profilePicture(newUser.getProfilePicture())
                .createdAt(newUser.getCreatedAt())
                .isAdmin(newUser.getIsAdmin()) // ===== AJOUT =====
                .build();
    }

    /**
     * Connexion d'un utilisateur existant
     * MODIFICATION : Ajout isAdmin dans la réponse
     */
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest,
                                  HttpServletRequest request) {

        val authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        val session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        val userDetails = (CustomUserDetails) authentication.getPrincipal();
        val user = userDetails.getUser();

        log.info("Connexion réussie pour: {} (Admin: {})", user.getUserName(), user.isAdmin());

        return LoginResponseDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .age(user.getAge())
                .profilePicture(user.getProfilePicture())
                .isAdmin(user.getIsAdmin()) // ===== AJOUT =====
                .build();
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        val session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            log.info("Session invalidée pour déconnexion");
        }
        SecurityContextHolder.clearContext();
        log.info("Déconnexion effectuée avec succès");
        return "Déconnexion réussie";
    }
}