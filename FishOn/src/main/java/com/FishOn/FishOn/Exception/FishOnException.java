package com.FishOn.FishOn.Exception;

import java.util.UUID;

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
}