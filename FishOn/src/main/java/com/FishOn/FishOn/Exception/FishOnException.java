package com.FishOn.FishOn.Exception;

import java.util.UUID;

import com.FishOn.FishOn.Model.PostModel;

public class FishOnException {

    // ============= UserException =============
    public static class EmailAlreadyExists extends Exception {
        public EmailAlreadyExists(String email) {
            super("L'email " + email + " est déjà pris");
        }
    }
    public static class UserAlreadyExists extends Exception {
        public UserAlreadyExists(String userName) {
            super("l'username" + userName + " est déjà pris");
        }
    }

    public static class UserNotFoundById extends Exception {
        public UserNotFoundById(UUID userId) {
            super("l'utilisateur avec l'ID " + userId + " n'existe pas");
        }
    }

    public static class UserNotFoundByUserName extends Exception {
        public UserNotFoundByUserName(String userName) {
            super("l'utilisateur " + userName + " n'existe pas");
        }
    }

    public static class UserNotFoundByEmail extends Exception {
        public UserNotFoundByEmail(String email) {
            super("l'utilisateur " + email + " n'existe pas");
        }
    }

    // ============= PostException =============
    public static class MissingTitleException extends Exception {
        public MissingTitleException(String title) {
            super("Titre obligatoire");
        }
    }

    public static class MissingDescriptionException extends Exception {
        public MissingDescriptionException(String description) {
            super("Description obligatoire");
        }
    }

    public static class MissingFishNameException extends Exception {
        public MissingFishNameException(String fishName) {
            super("fishName obligatoire");
        }
    }

    public static class PostNotFoundById extends Exception {
        public PostNotFoundById(UUID id) {
            super("La publication n'existe pas");
        }
    }

    public static class UnauthorizedModificationPost extends Exception {
        public UnauthorizedModificationPost() {
            super("N'est pas autorisé à modifier cette publication");
        }
    }

    public static class FishNameNotFound extends Exception {
        public FishNameNotFound(String fishName) {
            super("Ce poisson n'existe pas");
        }
    }

    public static class LocationNotFound extends Exception {
        public LocationNotFound(String location) {
            super("Cette localisation n'existe pas");
        }
    }

    // ============= CommentException =============
    public static class CommentNotFound extends Exception {
        public CommentNotFound(UUID commentId) {
            super("le commentaire " + commentId + " n'existe pas");
        }
    }

    public static class UnauthorizedAccess extends Exception {
        public UnauthorizedAccess() {
            super("N'est pas autorisé à modifier");
        }
    }

    // ============= AuthException =============
    public static class InvalidPassword extends Exception {
        public InvalidPassword() {
            super("Le mot de passe est incorrect");
        }
    }
}