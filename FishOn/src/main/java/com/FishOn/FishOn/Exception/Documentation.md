# Vue d'ensemble
__Couche__ `Exception` constitue le __système de gestion d'erreurs centralisé__.

Définit les __exceptions métier personalisées__ et leur __traitement global__, assurant une __cohérence__ dans la gestion des erreurs et une __expérience utilisateur__ optimale avec message d'erreur appropriés et des __codes de status__ `HTTP`.

# Architecture Exceptions
```bash
Exception/
├── FishOnException.java           # Définitions exceptions métier
└── FishOnExceptionHandler.java    # Gestionnaire global (@ControllerAdvice)
```

# Concept et Annotations
`@ControllerAdvice`
```java
@ControllerAdvice
public class FishOnExceptionHandler {
    // Gestionnaire global d'exceptions pour tous les controllers
    // Intercepte les exceptions non gérées
    // Centralise la logique de transformation exception → réponse HTTP
}
```

`@ExceptionHAndler`
```java
@ExceptionHandler(EmailAlreadyExists.class)
public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyExists e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
}
// Associe type d'exception à méthode de traitement
// Permet mapping personnalisé exception → réponse
```

# Pattern Exception Handler
- __Centralisation__ du traitement des erreurs.
- __Séparation__ logique métier / gestion erreurs.
- __Transformation__ exceptions → réponses HTTP.

# Code HTTP Sémantiques
```bash
400 BAD_REQUEST    → Données invalides (validation)
401 UNAUTHORIZED   → Échec authentification
403 FORBIDDEN      → Accès refusé (autorisation)  
404 NOT_FOUND      → Ressource inexistante
409 CONFLICT       → Conflit unicité
```
