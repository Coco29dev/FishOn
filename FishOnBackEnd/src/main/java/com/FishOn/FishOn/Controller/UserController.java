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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;
import java.util.List;

/**
 * Controller REST pour la gestion des utilisateurs
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Récupération du profil de l'utilisateur connecté
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UpdateUserResponseDTO getUser(Authentication authentication) {
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
                .isAdmin(user.getIsAdmin())
                .build();
    }

    /**
     * Modification du profil de l'utilisateur connecté
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UpdateUserResponseDTO updateUser(
            @Valid @RequestBody UpdateUserRequestDTO updateRequest,
            Authentication authentication)
            throws EmailAlreadyExists, UserAlreadyExists, UserNotFoundById {

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUserId = userDetails.getUser().getId();

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
    @PreAuthorize("isAuthenticated()")
    public String deleteUser(Authentication authentication, HttpServletRequest request)
            throws UserNotFoundById {

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
    @PreAuthorize("hasRole('ADMIN')")
    public List<UpdateUserResponseDTO> getAllUsers(Authentication authentication) {
        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUser = userDetails.getUser();

        log.info("Admin {} consulte la liste de tous les utilisateurs", currentUser.getUserName());

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
                .toList();
    }

    /**
     * Supprimer n'importe quel utilisateur (ADMIN UNIQUEMENT)
     * ✅ UNIFORMISÉ : @PreAuthorize au lieu de vérifications manuelles
     */
    @DeleteMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUserByAdmin(@PathVariable UUID userId, Authentication authentication)
            throws UserNotFoundById {

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var currentUser = userDetails.getUser();

        // Empêcher l'admin de se supprimer lui-même
        if (currentUser.getId().equals(userId)) {
            return "Impossible de supprimer son propre compte admin";
        }

        userService.deleteUser(userId);
        log.info("Admin {} a supprimé l'utilisateur {}", currentUser.getUserName(), userId);

        return "Utilisateur supprimé avec succès par l'administrateur";
    }
}