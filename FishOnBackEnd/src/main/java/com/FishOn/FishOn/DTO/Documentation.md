# Vue d'ensemble
__Couche__ `DTO` constitue la __couche communication__.

Définit __contrat d'interface__ entre le `frontend` et le `backend`, en __encapsulant__ les données échangés via l'`API REST`.

Cette __couche__ assure l'__isolation des modèles métier__, la __validation__ des entrées et la __sérialisation__ `JSON`.

## Contrat API
- __Définition des interfaces__ client-serveur.
- __Structure des requêtes/réponses__ `JSON`.
- __Versioning__ et évolutivité de l'API.
- __Documentation__ implicite des endpoints.

## Validation et sécurité
- __Validation déclarative__ avec `Jakarta Bean Validation`
- __Filtrage des données sensibles__ (exclusion mots de passe).
- __Contrôle__ des tailles et formats d'entrée.
- Messages d'erreur personnalisés.

## Isolation architectural
- __Découplage__ entre __modèles métier__ et `API`.
- __Stabilité__ de l'__interface__ publique.
- __Flexibilité__ d'évolution interne.
- __Transformation__ contrôlée des données.

# Architecture DTO
```bash
DTO/
├── Auth/               # Authentification et autorisation
│   ├── LoginRequestDTO
│   ├── LoginResponseDTO
│   ├── RegisterRequestDTO
│   └── RegisterResponseDTO
├── User/               # Gestion profil utilisateur
│   ├── UpdateUserRequestDTO
│   └── UpdateUserResponseDTO
├── Post/               # Publications de pêche
│   ├── PostCreateDTO
│   ├── PostUpdateDTO
│   └── PostResponseDTO
└── Comment/            # Système de commentaires
    ├── CommentCreateDTO
    ├── CommentUpdateDTO
    └── CommentResponseDTO
```

# Pattern DTO(Data Transfer Object)
- __Encapsulation__ des données de transfert.
- __Réduction__ du nombre d'appels réseau.
- __Optimisation__ de la sérialisation.
- __Contrôle__ de la structure des échanges.

# Pattern Request/Response
- __`DTOs` de requête__: Validation des entrées utilisateur.
- __`DTOs` de réponse__: Formatage des sorties contrôlé.
- __Séparation__ des responsabilités par direction.

# Pattern Validation
- __Validation déclarative__ avec annotations.
- __Messages personnalisés__ pour UX.
- __Règles métier__ exprimées au niveau `DTO`.

# Annotations de contraintes
```java
// Contraintes de nullité
@NotNull    // Valeur non null
@NotBlank   // Chaîne non null, non vide, sans espaces seulement

// Contraintes de taille
@Size(min = 1, max = 20)    // Longueur chaîne ou collection
@Min(value = 9)             // Valeur numérique minimum
@Max(value = 99)            // Valeur numérique maximum

// Contraintes de format
@Email      // Format email valide
```
# Sérialisation JSON avec Jackson
__Constructeur obligatoire__
```java
// Pour désérialisation JSON → Object
public LoginRequestDTO() {}

// Pour faciliter les tests et l'usage programmatique
public LoginRequestDTO(String email, String password) {
    this.email = email;
    this.password = password;
}
```

__Getters/Setters__
```java
// Pour sérialisation Object → JSON
public String getEmail() { return email; }

// Pour désérialisation JSON → Object
public void setEmail(String email) { this.email = email; }
```

# FLux de données
__Requête__
```bash
 JSON → DTO Request (validation) → Model → Service
 ```

__Réponse__
```bash
Service → Model → DTO Response → JSON
```
