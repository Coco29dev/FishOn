package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.DTO.User.UpdateUserRequestDTO;
import com.FishOn.FishOn.DTO.User.UpdateUserResponseDTO;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.UserService;
import com.FishOn.FishOn.Exception.FishOnException.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

/**
 * Controller REST pour la gestion des utilisateurs
 * Gère les opérations CRUD sur le profil utilisateur et la recherche d'utilisateurs
 *
 * Routes disponibles :
 * - GET /api/users/me : Récupération du profil utilisateur connecté (protégé)
 * - PUT /api/users/me : Modification du profil utilisateur connecté (protégé)
 * - DELETE /api/users/me : Suppression du compte utilisateur connecté (protégé)
 * - GET /api/users/search/{userName} : Recherche d'un utilisateur par nom (public)
 */
@RestController // Annotation Spring : répond automatiquement en JSON
@RequestMapping("/api/users") // Préfixe pour toutes les routes de ce controller
public class UserController {

    // Injection automatique du service métier pour les opérations utilisateur
    @Autowired
    private UserService userService;

    /**
     * Récupération du profil de l'utilisateur connecté
     * Endpoint protégé : nécessite une authentification valide
     *
     * @param authentication Objet d'authentification injecté automatiquement par Spring Security
     * @return UpdateUserResponseDTO contenant les données du profil utilisateur
     * @throws ResponseStatusException 401 si l'utilisateur n'est pas authentifié
     */
    @GetMapping("/me")
    public UpdateUserResponseDTO getUser(Authentication authentication) {

        // Vérification de sécurité : s'assurer que l'utilisateur est bien authentifié
        // Spring Security peut parfois passer un objet Authentication null ou non authentifié
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        // Vérification du type d'authentification : doit être notre CustomUserDetails
        // Protection contre les autres types d'authentification (OAuth, JWT, etc.)
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération sécurisée des données utilisateur depuis l'authentification
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserModel user = userDetails.getUser();

        // Construction de la réponse DTO avec toutes les données utilisateur
        return new UpdateUserResponseDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getProfilePicture(),
                user.getUpdatedAt() // Timestamp de la dernière modification
        );
    }

    /**
     * Modification du profil de l'utilisateur connecté
     * Endpoint protégé : seul l'utilisateur connecté peut modifier son propre profil
     *
     * @param updateRequest DTO contenant les nouvelles données (validées avec @Valid)
     * @param authentication Objet d'authentification pour identifier l'utilisateur
     * @return UpdateUserResponseDTO contenant les données mises à jour
     * @throws EmailAlreadyExists si le nouvel email est déjà utilisé par un autre utilisateur
     * @throws UserAlreadyExists si le nouveau username est déjà pris
     * @throws UserNotFoundById si l'utilisateur n'existe plus en base (cas rare)
     */
    @PutMapping("/me")
    public UpdateUserResponseDTO updateUser(
            @Valid @RequestBody UpdateUserRequestDTO updateRequest,
            Authentication authentication)
            throws EmailAlreadyExists, UserAlreadyExists, UserNotFoundById {

        // Vérification de sécurité identique à getUser()
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        // Vérification du type d'authentification : doit être notre CustomUserDetails
        // Protection contre les autres types d'authentification (OAuth, JWT, etc.)
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération de l'ID utilisateur depuis l'authentification
        // Garantit que l'utilisateur ne peut modifier que son propre profil
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUser().getId();

        // Transformation du DTO de requête en entité métier
        // Le mot de passe est null car géré séparément (endpoint dédié pour changement de mot de passe)
        UserModel updatedUser = new UserModel(
                updateRequest.getUserName(),
                updateRequest.getEmail(),
                updateRequest.getFirstName(),
                updateRequest.getLastName(),
                updateRequest.getAge(),
                null, // Mot de passe géré séparément pour sécurité
                updateRequest.getProfilePicture()
        );

        // Appel du service pour mise à jour en base de données
        // Le service gère : validation unicité email/username, sauvegarde BDD
        UserModel savedUser = userService.updateUser(currentUserId, updatedUser);

        // Construction de la réponse avec les données mises à jour
        return new UpdateUserResponseDTO(
                savedUser.getId(),
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getAge(),
                savedUser.getProfilePicture(),
                savedUser.getUpdatedAt() // Nouveau timestamp de modification
        );
    }

    /**
     * Suppression du compte de l'utilisateur connecté
     * Endpoint protégé : supprime définitivement le compte et toutes ses données
     * Action irréversible qui invalide immédiatement la session
     *
     * @param authentication Objet d'authentification pour identifier l'utilisateur
     * @param request HttpServletRequest pour accéder à la session HTTP
     * @return Message de confirmation de suppression
     * @throws UserNotFoundById si l'utilisateur n'existe plus en base
     */
    @DeleteMapping("/me")
    public String deleteUser(Authentication authentication, HttpServletRequest request)
            throws UserNotFoundById {

        // Vérification de sécurité identique à getUser()
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        // Vérification du type d'authentification : doit être notre CustomUserDetails
        // Protection contre les autres types d'authentification (OAuth, JWT, etc.)
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Type d'authentification invalide");
        }

        // Récupération de l'ID utilisateur depuis l'authentification
        // Garantit que l'utilisateur ne peut modifier que son propre profil
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUser().getId();

        // Suppression définitive de l'utilisateur et de toutes ses données liées
        // Le service gère la suppression en cascade (posts, commentaires, etc.)
        userService.deleteUser(currentUserId);

        // Nettoyage immédiat de la session HTTP après suppression
        // L'utilisateur ne peut plus utiliser sa session après suppression du compte
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalidation du cookie de session
        }

        // Nettoyage du contexte Spring Security
        SecurityContextHolder.clearContext();

        return "Compte supprimé avec succès";
    }

    /**
     * Recherche d'un utilisateur par son nom d'utilisateur
     * Endpoint PUBLIC : accessible sans authentification
     * Permet de trouver et consulter le profil public d'autres utilisateurs
     *
     * @param userName Nom d'utilisateur à rechercher (paramètre d'URL)
     * @return UpdateUserResponseDTO contenant les données publiques de l'utilisateur
     * @throws UserNotFoundByUserName si aucun utilisateur ne correspond à ce nom
     */
    @GetMapping("/search/{userName}")
    public UpdateUserResponseDTO getUserByUserName(@PathVariable String userName)
            throws UserNotFoundByUserName {

        // Recherche de l'utilisateur par nom d'utilisateur
        // Pas de vérification d'authentification car endpoint public
        UserModel user = userService.getByUserName(userName);

        // Retour des données utilisateur (profil public)
        // Même DTO que pour les autres endpoints pour cohérence
        return new UpdateUserResponseDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getProfilePicture(),
                user.getUpdatedAt()
        );
    }
}