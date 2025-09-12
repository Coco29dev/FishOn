package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Service.AuthService;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.DTO.Auth.*;
import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.Exception.FishOnException.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion de l'authentification
 * LOMBOK UTILISÉ :
 * @RequiredArgsConstructor : Injection par constructeur automatique pour les champs final
 * @Slf4j : Logger automatique disponible via log.info(), log.error(), etc.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // LOMBOK : Remplace @Autowired, génère constructeur avec champs final
@Slf4j // LOMBOK : Logger automatique, variable 'log' disponible partout
public class AuthController {

    // LOMBOK : final + @RequiredArgsConstructor = injection automatique
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    /**
     * Inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO registerRequest)
            throws EmailAlreadyExists, UserAlreadyExists {

        // LOMBOK : Utilisation du Builder pattern généré automatiquement
        UserModel user = UserModel.builder()
                .userName(registerRequest.getUserName())
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .age(registerRequest.getAge())
                .password(registerRequest.getPassword())
                .profilePicture(registerRequest.getProfilePicture())
                // isAdmin = false par défaut (Builder.Default dans UserModel)
                .build();

        UserModel newUser = authService.register(user);

        // LOMBOK : @Slf4j permet d'utiliser 'log' directement
        log.info("Nouvel utilisateur inscrit: {} (Admin: {})", newUser.getUserName(), newUser.isAdmin());

        // LOMBOK : Utilisation du Builder pattern pour la réponse
        return RegisterResponseDTO.builder()
                .id(newUser.getId())
                .userName(newUser.getUserName())
                .email(newUser.getEmail())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .age(newUser.getAge())
                .profilePicture(newUser.getProfilePicture())
                .createdAt(newUser.getCreatedAt())
                .isAdmin(newUser.getIsAdmin())
                .build();
    }

    /**
     * Connexion d'un utilisateur existant
     */
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest,
                                  HttpServletRequest request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var user = userDetails.getUser();

        log.info("Connexion réussie pour: {} (Admin: {})", user.getUserName(), user.isAdmin());

        return LoginResponseDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .age(user.getAge())
                .profilePicture(user.getProfilePicture())
                .isAdmin(user.getIsAdmin())
                .build();
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            log.info("Session invalidée pour déconnexion");
        }
        SecurityContextHolder.clearContext();
        log.info("Déconnexion effectuée avec succès");
        return "Déconnexion réussie";
    }
}