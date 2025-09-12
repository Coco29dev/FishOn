package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.DTO.User.UpdateUserRequestDTO;
import com.FishOn.FishOn.DTO.User.UpdateUserResponseDTO;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.UserService;
import com.FishOn.FishOn.Exception.FishOnException.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;
import java.util.List;

/**
 * Controller REST pour la gestion des utilisateurs
 * LOMBOK UTILISÉ :
 * @RequiredArgsConstructor : Injection par constructeur automatique
 * @Slf4j : Logger automatique
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor // LOMBOK : Remplace @Autowired
@Slf4j // LOMBOK : Logger automatique
public class UserController {

    private final UserService userService;

    /**
     * Récupération du profil de l'utilisateur connecté
     */
    @GetMapping("/me")
    public UpdateUserResponseDTO getUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var user = userDetails.getUser();

        return UpdateUserResponseDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .age(user.getAge())
                .profilePicture(user.getProfilePicture())
                .updatedAt(user.getUpdatedAt())
                .isAdmin(user.getIsAdmin()) // Ajout du statut admin
                .build();
    }

    /**
     * Modification du profil de l'utilisateur connecté
     */
    @PutMapping("/me")
    public UpdateUserResponseDTO updateUser(
            @Valid @RequestBody UpdateUserRequestDTO updateRequest,
            Authentication authentication)
            throws EmailAlreadyExists, UserAlreadyExists, UserNotFoundById {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        // LOMBOK : Utilisation du Builder pattern
        var updatedUser = UserModel.builder()
                .userName(updateRequest.getUserName())
                .email(updateRequest.getEmail())
                .firstName(updateRequest.getFirstName())
                .lastName(updateRequest.getLastName())
                .age(updateRequest.getAge())
                .profilePicture(updateRequest.getProfilePicture())
                .build();

        var savedUser = userService.updateUser(currentUserId, updatedUser);

        return UpdateUserResponseDTO.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .age(savedUser.getAge())
                .profilePicture(savedUser.getProfilePicture())
                .updatedAt(savedUser.getUpdatedAt())
                .isAdmin(savedUser.getIsAdmin())
                .build();
    }

    /**
     * Suppression du compte de l'utilisateur connecté
     */
    @DeleteMapping("/me")
    public String deleteUser(Authentication authentication, HttpServletRequest request)
            throws UserNotFoundById {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

        userService.deleteUser(currentUserId);

        // Nettoyage de la session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        return "Compte supprimé avec succès";
    }

    /**
     * Recherche d'un utilisateur par nom d'utilisateur (PUBLIC)
     */
    @GetMapping("/search/{userName}")
    public UpdateUserResponseDTO getUserByUserName(@PathVariable String userName)
            throws UserNotFoundByUserName {

        var user = userService.getByUserName(userName);

        return UpdateUserResponseDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .age(user.getAge())
                .profilePicture(user.getProfilePicture())
                .updatedAt(user.getUpdatedAt())
                .isAdmin(user.getIsAdmin())
                .build();
    }

    // =============== ENDPOINTS ADMIN UNIQUEMENT ===============

    /**
     * Liste tous les utilisateurs (ADMIN UNIQUEMENT)
     */
    @GetMapping("/admin/all")
    public List<UpdateUserResponseDTO> getAllUsers(Authentication authentication) {
        // Vérification authentification
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUser = userDetails.getUser();

        // Vérification admin
        if (!currentUser.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux administrateurs");
        }

        var users = userService.getAllUsers();

        return users.stream()
                .map(user -> UpdateUserResponseDTO.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .age(user.getAge())
                        .profilePicture(user.getProfilePicture())
                        .updatedAt(user.getUpdatedAt())
                        .isAdmin(user.getIsAdmin())
                        .build())
                .toList(); // Java 16+ ou .collect(Collectors.toList())
    }

    /**
     * Supprimer n'importe quel utilisateur (ADMIN UNIQUEMENT)
     */
    @DeleteMapping("/admin/{userId}")
    public String deleteUserByAdmin(@PathVariable UUID userId, Authentication authentication)
            throws UserNotFoundById {

        // Vérification authentification
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUser = userDetails.getUser();

        // Vérification admin
        if (!currentUser.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux administrateurs");
        }

        // Empêcher l'admin de se supprimer lui-même
        if (currentUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de supprimer son propre compte admin");
        }

        userService.deleteUser(userId);
        log.info("Admin {} a supprimé l'utilisateur {}", currentUser.getUserName(), userId);

        return "Utilisateur supprimé avec succès par l'administrateur";
    }
}