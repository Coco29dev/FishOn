package com.FishOn.FishOn.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.FishOn.FishOn.DTO.Auth.*;
import com.FishOn.FishOn.Exception.FishOnException.*;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.AuthService;
import com.FishOn.FishOn.Config.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion de l'authentification
 * Gère l'inscription, la connexion et la déconnexion des utilisateurs
 *
 * Routes disponibles :
 * - POST /api/auth/register : Inscription d'un nouvel utilisateur
 * - POST /api/auth/login : Connexion d'un utilisateur existant
 * - POST /api/auth/logout : Déconnexion de l'utilisateur courant
 */
@RestController // Annotation Spring : répond automatiquement en JSON
@RequestMapping("/api/auth") // Préfixe pour toutes les routes de ce controller
public class AuthController {

    // Injection automatique des dépendances Spring
    @Autowired
    private AuthService authService; // Service métier pour l'authentification

    @Autowired
    private AuthenticationManager authenticationManager; // Gestionnaire d'authentification Spring Security

    /**
     * Inscription d'un nouvel utilisateur
     *
     * @param registerRequest DTO contenant les données d'inscription (validées avec @Valid)
     * @return RegisterResponseDTO contenant les informations de l'utilisateur créé (sans mot de passe)
     * @throws EmailAlreadyExists si l'email est déjà utilisé
     * @throws UserAlreadyExists si le nom d'utilisateur est déjà pris
     */
    @PostMapping("/register")
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO registerRequest)
            throws EmailAlreadyExists, UserAlreadyExists {

        // Transformation du DTO en entité métier UserModel
        UserModel user = new UserModel(
                registerRequest.getUserName(),
                registerRequest.getEmail(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getAge(),
                registerRequest.getPassword(), // Sera hashé dans le service
                registerRequest.getProfilePicture()
        );

        // Appel du service pour enregistrer l'utilisateur en base de données
        // Le service gère : validation unicité, hashage mot de passe, sauvegarde BDD
        UserModel newUser = authService.register(user);

        // Construction de la réponse DTO (sans mot de passe pour sécurité)
        return new RegisterResponseDTO(
                newUser.getId(),
                newUser.getUserName(),
                newUser.getEmail(),
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getAge(),
                newUser.getProfilePicture(),
                newUser.getCreatedAt()
        );
    }

    /**
     * Connexion d'un utilisateur existant
     * Crée une session HTTP pour maintenir l'état d'authentification
     *
     * @param loginRequest DTO contenant email et mot de passe
     * @param request HttpServletRequest pour accéder à la session HTTP
     * @return LoginResponseDTO contenant les informations de l'utilisateur connecté
     */
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest,
                                  HttpServletRequest request) {

        // Spring Security vérifie automatiquement les identifiants
        // Si incorrects, une exception est levée automatiquement (gérée par le GlobalExceptionHandler)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),    // Identifiant (email dans notre cas)
                        loginRequest.getPassword()  // Mot de passe en clair (Spring Security s'occupe de la vérification)
                )
        );

        // Stockage de l'authentification dans le contexte Spring Security
        // Permet aux autres endpoints protégés de récupérer l'utilisateur connecté
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Création explicite de la session HTTP pour persister l'authentification
        // true = créer une nouvelle session si elle n'existe pas
        HttpSession session = request.getSession(true);

        // Liaison explicite du contexte Spring Security à la session HTTP
        // Nécessaire pour que Spring Security retrouve l'authentification lors des prochaines requêtes
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        // Logs de debug pour vérifier la création de session (à retirer en production)
        System.out.println("=== SESSION CRÉÉE ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Session new: " + session.isNew());

        // Récupération des données utilisateur depuis l'objet Authentication
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserModel user = userDetails.getUser();

        // Construction de la réponse avec les données utilisateur (sans mot de passe)
        return new LoginResponseDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getProfilePicture()
        );
    }

    /**
     * Déconnexion de l'utilisateur courant
     * Invalide la session HTTP et nettoie le contexte Spring Security
     *
     * @param request HttpServletRequest pour accéder à la session
     * @return Message de confirmation de déconnexion
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Récupération de la session existante (false = ne pas en créer une nouvelle)
        HttpSession session = request.getSession(false);

        if (session != null) {
            // Invalidation de la session HTTP côté serveur
            // Supprime toutes les données de session et rend le cookie invalide
            session.invalidate();
        }

        // Nettoyage du contexte Spring Security
        // Supprime l'authentification du thread courant
        SecurityContextHolder.clearContext();

        return "Déconnexion réussie";
    }
}