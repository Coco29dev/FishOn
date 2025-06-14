package com.FishOn.FishOn.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.FishOn.FishOn.Exception.FishOnException.*;

@ControllerAdvice
public class FishOnExceptionHandler {

    // ========= UserException =========
    @ExceptionHandler(EmailAlreadyExists.class)
    public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    // HttpsStatus.CONFLICT = 409 CONFLICT : Email/Username déjà pris
    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    // HttpsStatus.CONFLICT = 409 CONFLICT : Email/Username déjà pris
    @ExceptionHandler(UserNotFoundById.class)
    public ResponseEntity<String> handleUserNotFoundById(UserNotFoundById e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    // HttpsStatus.NOT_FOUND = 404 NOT_FOUND : Ressource inexistante
    @ExceptionHandler(UserNotFoundByEmail.class)
    public ResponseEntity<String> handleUserNotFoundByEmail(UserNotFoundByEmail e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
    }
    // HttpsStatus.UNAUTHORIZED = 401 UNAUTHORIZED : Login incorrect

    // ========= AuthException =========
    @ExceptionHandler(InvalidPassword.class)
    public ResponseEntity<String> handleInvalidPassword(InvalidPassword e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
    }
    // HttpsStatus.UNAUTHORIZED = 401 UNAUTHORIZED : Login incorrect
    // ========= PostException =========
    @ExceptionHandler(PostNotFoundById.class)
    public ResponseEntity<String> handlePostNotFoundById(PostNotFoundById e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    // HttpsStatus.NOT_FOUND = 404 NOT_FOUND : Ressource inexistante
    @ExceptionHandler(UnauthorizedModificationPost.class)
    public ResponseEntity<String> handleUnauthorizedModificationPost(UnauthorizedModificationPost e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
    // HttpStatus.FORBIDDEN = 403 FORBIDDEN : Pas le droit de modifier

    // ========= CommentException =========
    @ExceptionHandler(CommentNotFound.class)
    public ResponseEntity<String> handleCommentNotFound(CommentNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    // HttpsStatus.NOT_FOUND = 404 NOT_FOUND : Ressource inexistante
    @ExceptionHandler(UnauthorizedAccess.class)
    public ResponseEntity<String> handleUnauthorizedAccess(UnauthorizedAccess e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
    // HttpStatus.FORBIDDEN = 403 FORBIDDEN : Pas le droit de modifier
}