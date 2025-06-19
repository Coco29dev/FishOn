# Vue d'ensemble
Couche `Service` constitue le __coeur de la logique métier__.

__Interface__ entre les `Controllers` et les `Repositories` en __encapsulant__ toutes les __règle business__ et __validations métiers__.

## Logique métier centralisée
- __Validation des données__ avant __persistance__.
- __Règle business__ spécifique au domaine.
- __Orchestration__ des opérations complexes.
- __Gestion des transactions__ implicites.

## Abstraction et découplage
- __Interface stable__ pour les `controllers`.
- __Indépendance__ vis-à-vis de la couche __persistance__.
- __Réutilisabilité__ de la __logique métier__.
- __Testabilité__ amélioré

# Architecture Services
```bash
Service/
├── AuthService      # Authentification et sécurité
├── UserService      # Gestion des utilisateurs
├── PostService      # Gestion des publications
└── CommentService   # Gestion des commentaires
```

# Concept et Annotations Spring
`@Service`
```java
@Service
public class UserService {
    // Marque la classe comme composant Spring de type "Service"
    // Gestion automatique du cycle de vie par le conteneur Spring
    // Injection automatique dans d'autres composants
}
```

`@Autowired`
```java
@Autowired
private UserRepository userRepository;
// Injection de dépendance automatique
// Spring résout et injecte l'implémentation appropriée
// Couplage faible entre les couches
```

# Pattern Service Layer
- __Encapsulation__ de la __logique métier__.
- __Transaction boundary__ pour les opérations complexes.
- __Validation centralisée__ des __règles business__.

# Pattern Facade
- __Interfarce__ simplifiée pour les `controllers`.
- __Orchestration__ multiple `repositories`.
- __Masquage__ complexité interne.

# AuthService - Authentification
```java
// Responsabilités spécialisées
- register()         // Délégation vers UserService
- updatePassword()   // Gestion sécurisée des mots de passe
```
- __Délégation__ vers `UserService` pour inscription.
- __Validation__ mot de passe actuel avant modification.
- __Encodage__ `BCrypt` nouveau mot de passe.

# UserService - Gestion utilisateurs
```java
// CRUD complet
- createUser()       // Validation unicité + encodage mot de passe
- updateUser()       // Validation changements + préservation données
- deleteUser()       // Suppression avec cascade automatique

// Requêtes métier
- getByUserName()    // Recherche pour profils publics
- getByEmail()       // Recherche pour authentification
```
- __Unicité email/username__ lors de la création et mlodification.
- __Encodage automatique__ mot de passe.

# PostService - Gestion des publications
```java
// Validation métier centralisée
- validateData()     // Contrôle champs obligatoires

// CRUD avec autorisation
- createPost()       // Association utilisateur + validation
- updatePost()       // Vérification propriété + validation
- deletePost()       // Contrôle autorisation

// Recherches spécialisées
- getByFishName()    // Filtrage par espèce
- getByLocation()    // Filtrage géographique
```
- __Vérification de propriété__ avant modification/suppression.
- __Validation systématique__ des données obligatoires.
- __Contrôle d'existence__ des entités liées.

# CommentService - Gestion des commentaires
```java
// CRUD avec relations
- createComment()    // Association user + post
- updateComment()    // Vérification propriété
- deleteComment()    // Contrôle autorisation

// Requêtes relationnelles
- getByUserId()      // Historique utilisateur
- getByPostId()      // Commentaires d'une publication
```
