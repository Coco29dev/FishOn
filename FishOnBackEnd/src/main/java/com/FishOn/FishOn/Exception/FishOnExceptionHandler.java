package com.FishOn.FishOn.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.FishOn.FishOn.Exception.FishOnException.*;

@ControllerAdvice
public class FishOnExceptionHandler {

    // ========= UserException =========
    
    // HttpStatus.CONFLICT = 409 CONFLICT : Email déjà pris
    @ExceptionHandler(EmailAlreadyExists.class)
    public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // HttpStatus.CONFLICT = 409 CONFLICT : Username déjà pris
    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // HttpStatus.NOT_FOUND = 404 NOT_FOUND : Utilisateur inexistant par ID
    @ExceptionHandler(UserNotFoundById.class)
    public ResponseEntity<String> handleUserNotFoundById(UserNotFoundById e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // HttpStatus.NOT_FOUND = 404 NOT_FOUND : Utilisateur inexistant par Username
    @ExceptionHandler(UserNotFoundByUserName.class)
    public ResponseEntity<String> handleUserNotFoundByUserName(UserNotFoundByUserName e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // HttpStatus.UNAUTHORIZED = 401 UNAUTHORIZED : Login incorrect (email)
    @ExceptionHandler(UserNotFoundByEmail.class)
    public ResponseEntity<String> handleUserNotFoundByEmail(UserNotFoundByEmail e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email incorrect");
    }

    // ========= AuthException =========
    
    // HttpStatus.UNAUTHORIZED = 401 UNAUTHORIZED : Login incorrect (mot de passe)
    @ExceptionHandler(InvalidPassword.class)
    public ResponseEntity<String> handleInvalidPassword(InvalidPassword e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
    }

    // ========= PostException =========
    
    // HttpStatus.BAD_REQUEST = 400 BAD_REQUEST : Titre manquant
    @ExceptionHandler(MissingTitleException.class)
    public ResponseEntity<String> handleMissingTitleException(MissingTitleException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // HttpStatus.BAD_REQUEST = 400 BAD_REQUEST : Description manquante
    @ExceptionHandler(MissingDescriptionException.class)
    public ResponseEntity<String> handleMissingDescriptionException(MissingDescriptionException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // HttpStatus.BAD_REQUEST = 400 BAD_REQUEST : Nom de poisson manquant
    @ExceptionHandler(MissingFishNameException.class)
    public ResponseEntity<String> handleMissingFishNameException(MissingFishNameException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(MissingPhotoException.class)
    public ResponseEntity<String> handleMissingPhotoException(MissingPhotoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // HttpStatus.NOT_FOUND = 404 NOT_FOUND : Post inexistant
    @ExceptionHandler(PostNotFoundById.class)
    public ResponseEntity<String> handlePostNotFoundById(PostNotFoundById e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // HttpStatus.FORBIDDEN = 403 FORBIDDEN : Pas autorisé à modifier ce post
    @ExceptionHandler(UnauthorizedModificationPost.class)
    public ResponseEntity<String> handleUnauthorizedModificationPost(UnauthorizedModificationPost e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    // HttpStatus.NOT_FOUND = 404 NOT_FOUND : Nom de poisson inexistant
    @ExceptionHandler(FishNameNotFound.class)
    public ResponseEntity<String> handleFishNameNotFound(FishNameNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // HttpStatus.NOT_FOUND = 404 NOT_FOUND : Localisation inexistante
    @ExceptionHandler(LocationNotFound.class)
    public ResponseEntity<String> handleLocationNotFound(LocationNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // ========= CommentException =========
    
    // HttpStatus.NOT_FOUND = 404 NOT_FOUND : Commentaire inexistant
    @ExceptionHandler(CommentNotFound.class)
    public ResponseEntity<String> handleCommentNotFound(CommentNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // HttpStatus.FORBIDDEN = 403 FORBIDDEN : Pas autorisé à modifier ce commentaire
    @ExceptionHandler(UnauthorizedAccess.class)
    public ResponseEntity<String> handleUnauthorizedAccess(UnauthorizedAccess e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}