# FishOn
__Réseau Social__ dédié aux passionnés de __pêche__, permettant aux __utilisateurs__ de __partager__ leurs prises, __commenter__ les publications et __gérer__ leur profil avec __système d'administration__ intégré.

# Stack Technique
- __Framework__: `Spring Boot 3.5.0`.
- __Langage__: `Java 21`.
- __Base de données__: `PostgreSQL`.
- __Sécurité__: `Spring Security` avec __session `HTTP`__.
- __Architecture__: `REST API` avec pattern `MVC`.
- __Build__: `Maven`.
- __Productivité__: `Lombok` pour réduction du __boilerplate code__.

# Architecture
__Architecture en couches__(layered architecture) respectant les principes __SOLID__ et la séparation des responsabilités avec __annotations Lombok__ pour simplification du code.

```bash
┌─────────────────────────────────────────┐
│           COUCHE CONTROLLERS            │
│        (Présentation / API REST)        │
│                                         │
│  • AuthController                       │
│  • UserController (+ endpoints admin)   │
│  • PostController (+ endpoints admin)   │
│  • CommentController (+ endpoints admin)│
│                                         │
│  Responsabilités :                      │
│  - Exposer les endpoints REST           │
│  - Gérer les requêtes/réponses HTTP     │
│  - Valider les données d'entrée         │
│  - Contrôler l'autorisation par rôle    │
│  - Sérialiser en JSON                   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│             COUCHE DTOs                 │
│        (Transfert de données)           │
│                                         │
│  • Auth (Login, Register)               │
│  • User (Update Request/Response)       │
│  • Post (Create, Update, Response)      │
│  • Comment (Create, Update, Response)   │
│                                         │
│  Responsabilités :                      │
│  - Isoler l'API des modèles internes    │
│  - Valider les données (annotations)    │
│  - Contrôler ce qui est exposé          │
│  - Simplifier avec @Data, @Builder      │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            COUCHE SERVICES              │
│           (Logique métier)              │
│                                         │
│  • AuthService                          │
│  • UserService (+ méthodes admin)       │
│  • PostService (+ méthodes admin)       │
│  • CommentService (+ méthodes admin)    │
│                                         │
│  Responsabilités :                      │
│  - Implémenter la logique business      │
│  - Valider les règles métier            │
│  - Implémentation CRUD                  │
│  - Gérer les autorisations admin        │
│  - Orchestrer les opérations complexes  │
│  - Gérer les transactions               │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          COUCHE REPOSITORIES            │
│         (Accès aux données)             │
│                                         │
│  • UserRepository                       │
│  • PostRepository                       │
│  • CommentRepository                    │
│                                         │
│  Responsabilités :                      │
│  - Abstraire l'accès aux données        │
│  - Exécuter les requêtes personnalisées │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            COUCHE MODELS                │
│          (Entités JPA)                  │
│                                         │
│  • BaseModel (classe abstraite)         │
│  • UserModel (+ champ isAdmin)          │
│  • PostModel                            │
│  • CommentModel                         │
│                                         │
│  Responsabilités :                      │
│  - Représenter les tables en base       │
│  - Définir les relations entre entités  │
│  - Gérer les contraintes de données     │
│  - Simplifier avec annotations Lombok   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            COUCHE CONFIG                │
│      (Configuration & Initialisation)   │
│                                         │
│  • SecurityConfig                       │
│  • CustomUserDetails                    │
│  • CustomUserDetailsService             │
│  • AdminDataInitializer                 │
│  • DataInitializer                      │
│                                         │
│  Responsabilités :                      │
│  - Configuration Spring Security        │
│  - Initialisation données de test       │
│  - Création compte admin par défaut     │
└─────────────────────────────────────────┘
```

# Structure packages
```bash
com.FishOn.FishOn/
├── Config/          # Configuration Spring Security + Initialisation
├── Controller/      # API REST endpoints (+ endpoints admin)
├── DTO/            # Data Transfer Objects avec Lombok
├── Exception/      # Gestion d'erreurs centralisée
├── Model/          # Entités JPA avec Lombok
├── Repository/     # Accès aux données
└── Service/        # Logique métier (+ méthodes admin)
```

# Lombok - Productivité et Réduction du Boilerplate
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

__Apporte__ :
- __Génération automatique__ : Getters, setters, constructeurs, toString, equals/hashCode.
- __Pattern Builder__ : Construction flexible des objets.
- __Logging__ : Logger automatique avec `@Slf4j`.
- __Injection__ : Constructeur automatique avec `@RequiredArgsConstructor`.

__Utilisé pour__:
- __Réduire__ drastiquement le code boilerplate.
- __Améliorer__ la lisibilité du code métier.
- __Faciliter__ la maintenance des entités et DTOs.
- __Standardiser__ la construction des objets.

# Système d'Administration
__Architecture à deux niveaux__ d'autorisation :
- __Utilisateurs normaux__ : `ROLE_USER`
- __Administrateurs__ : `ROLE_ADMIN` + `ROLE_USER`

## Fonctionnalités Admin
- __Gestion utilisateurs__ : Voir tous, supprimer (sauf soi-même)
- __Modération contenu__ : Supprimer posts/commentaires
- __Bypass autorisations__ : Modifier/supprimer contenu de tous
- __Compte par défaut__ : Création automatique au démarrage

## Endpoints Admin
```bash
# Gestion utilisateurs
GET    /api/users/admin/all           # Liste tous les utilisateurs
DELETE /api/users/admin/{userId}      # Supprimer utilisateur

# Modération posts
DELETE /api/posts/admin/{postId}      # Supprimer publication

# Modération commentaires  
DELETE /api/comments/admin/{commentId} # Supprimer commentaire
```

# spring-boot-starter-security
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
__Apporte__ :
- __Authentification/Autorisation__ : Système de sécurité complet avec rôles.
- `BCrypt` : Encodage sécurisé des mots de passe.
- __Session Management__ : Gestion des __sessions `HTTP`__.
- `CORS/CSRF` : Protection contre les attaques web.
- __Annotations__ : `@PreAuthorize` pour contrôle d'accès.

__Utilisé pour__:
- __Protéger__ les endpoints (authentification + rôles).
- __Gérer__ les sessions utilisateur.
- __Encoder__ les mots de passe.
- __Configuration `CORS`__ pour le frontend React.
- __Différencier__ utilisateurs et administrateurs.

# spring-boot-starter-data-jpa
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

__Apporte__:
- `Spring Data JPA` : Repositories automatiques.
- `Hibernate` : ORM (Object-Relational Mapping).
- __Transactions__ : Gestion automatique des transactions.
- `Query Methods` : Méthodes de requête dérivées du nom.

__Utilisé pour__:
- __Mapper__ les entités Java vers les tables de base de données.
- __Générer__ automatiquement les méthodes CRUD.
- __Créer__ des requêtes personnalisées (`findByEmail`, `findByUserName`).
- __Gérer__ les relations entre entités (`@OneToMany`, `@ManyToOne`).

## Relations Entités
- __User__ <-> __Post__ : `@OneToMany` / `@ManyToOne`.
- __Post__ <-> __Comment__ : `@OneToMany` / `@ManyToOne`.
- __User__ <-> __Comment__ : `@OneToMany` / `@ManyToOne`.

# spring-boot-starter-validation
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

__Apporte__:
- `Jakarta Bean Validation` : Validation déclarative.
- `Hibernate Validator` : Implémentation des validations.
- __Annotations__ : `@NotNull`, `@Email`, `@Size`, `@Min`, `@Max`.

__Utilisé pour__:
- __Valider__ automatiquement les `DTOs` avec `@Valid`.
- __Définir__ des règles de validation avec des __annotations__.
- __Générer__ des messages d'erreur personnalisés.

# PostgreSQL Driver
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

__Apporte__:
- __Connecteur `PostgreSQL`__ : Communication avec la __base de données__.
- __Dialect automatique__ : `Hibernate` adapte `SQL` pour `PostgreSQL`.
- __Types spécialisés__ : Support des types `PostgreSQL` (`UUID`, `JSON`, etc.).

__Utilisé pour__:
- __Connecter__ l'application à la base de données `PostgreSQL`.
- __Exécuter__ les requêtes `SQL` générées par `Hibernate`.
- __Gérer__ les types de données spécifiques (`UUID` pour les identifiants).

# Couche Controllers
__Interface__ entre le __client `HTTP`__ et l'application avec __contrôle d'accès par rôles__.
- __Exposition__ endpoints `REST`.
- __Validation__ données d'entrée (`@Valid`).
- __Gestion__ authentification et autorisation.
- __Endpoints admin__ protégés par `@PreAuthorize("hasRole('ADMIN')")`.
- __Transformer Entity__ -> `DTO` pour les réponses.

# Couche DTO avec Lombok
__Contrat__ d'__interface__ entre __frontend__ et __backend__ simplifié par Lombok.

```java
@Data // @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@NoArgsConstructor // Constructeur vide pour Jackson
@AllArgsConstructor // Constructeur avec tous les paramètres
@Builder // Pattern Builder
public class LoginRequestDTO {
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
    
    @NotBlank(message = "Mot de passe est obligatoire")
    @Size(min = 5, message = "Le mot de passe doit contenir minimum 5 caractères")
    private String password;
}
```

- __Définir__ la structure des données échangées.
- __Valider__ les entrées utilisateurs.
- __Isoler__ les modèles internes de l'`API` publique.
- __Exclure__ les données sensibles (mots de passe).
- __Réduire__ le code boilerplate avec Lombok.

# Couche Service
__Logique Métier__ avec __méthodes admin__ dédiées.
- __Implémentation__ règles business.
- __Implémentation__ CRUD standard + admin.
- __Validation__ données métier.
- __Orchestration__ appels `repositories`.
- __Gestion__ transactions.
- __Contrôle autorisations__ (propriété vs admin).

## Pattern Service avec Lombok
```java
@Service
@RequiredArgsConstructor // Injection automatique
@Slf4j // Logger automatique
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Méthodes CRUD standard
    public UserModel createUser(UserModel user) { ... }
    
    // Méthodes admin spécifiques
    public List<UserModel> getAllUsers() { ... }
    public UserModel createAdmin(UserModel adminUser) { ... }
}
```

# Couche Repositories
__Abstraction__ de l'accès aux données.
- __Création__ requêtes personnalisées.
- __Abstraire__ la technologie de persistance.

# Couche Models avec Lombok
__Représentation__ des données en __base de données__ simplifiée.

```java
@Entity
@Table(name = "users")
@Getter @Setter // Lombok : getters/setters automatiques
@NoArgsConstructor // Lombok : constructeur vide pour JPA
@AllArgsConstructor 
@ToString(exclude = {"password", "posts", "comments"}) // Lombok : toString sans données sensibles
@EqualsAndHashCode(callSuper = true, exclude = {"posts", "comments"})
@Builder // Lombok : Pattern Builder
public class UserModel extends BaseModel {
    
    @Column(unique = true, nullable = false)
    private String userName;
    
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    
    @Column(nullable = false)
    @Builder.Default // Valeur par défaut pour Builder
    private Boolean isAdmin = false;
    
    // Relations JPA avec valeurs par défaut Lombok
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostModel> posts = new ArrayList<>();
    
    // Méthodes métier
    public boolean isAdmin() {
        return Boolean.TRUE.equals(isAdmin);
    }
}
```

- __Mapper__ les tables de __base de données__.
- __Définir__ les contraintes.
- __Gestion__ relations (`@OneToMany`, `@ManyToOne`).
- __Champ admin__ : `isAdmin` pour différenciation des rôles.

# Configuration et Initialisation
__Système d'initialisation__ des données avec __compte admin__ par défaut.

## AdminDataInitializer
```java
@Component
@RequiredArgsConstructor // Lombok : injection automatique
@Slf4j // Lombok : logger automatique
public class AdminDataInitializer {
    
    private final UserService userService;
    
    @PostConstruct
    public void initializeAdmin() {
        try {
            userService.getByUserName("admin");
            log.info("Administrateur par défaut déjà existant");
        } catch (Exception e) {
            var adminUser = UserModel.builder() // Lombok Builder
                    .userName("admin")
                    .email("admin@fishon.com")
                    .firstName("Super")
                    .lastName("Admin")
                    .age(30)
                    .password("Admin123!")
                    .isAdmin(true)
                    .build();
                    
            userService.createAdmin(adminUser);
            log.info("Administrateur créé - Username: admin, Password: Admin123!");
        }
    }
}
```

## DataInitializer
__Orchestrateur__ de l'initialisation des données de test :
- __10 utilisateurs__ de démonstration
- __30 publications__ (3 par utilisateur)
- __60 commentaires__ (2 par publication)

# Annotations stéréotype
```java
@RestController  // Composant Spring + réponses JSON automatiques
@Service        // Logique métier
@Repository     // Accès aux données
@Configuration  // Configuration Spring
@Component      // Composant générique
```

# Annotations Lombok Principales
```java
@Data           // @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@Builder        // Pattern Builder pour construction flexible
@RequiredArgsConstructor // Constructeur avec champs final (injection dépendances)
@Slf4j          // Logger automatique
@NoArgsConstructor // Constructeur vide (obligatoire pour JPA/Jackson)
@AllArgsConstructor // Constructeur avec tous les paramètres
```

# Annotations de Sécurité
```java
@PreAuthorize("isAuthenticated()")    // Authentification requise
@PreAuthorize("hasRole('ADMIN')")     // Rôle admin requis
@PreAuthorize("hasRole('USER')")      // Rôle utilisateur requis
```

# Pattern MVC(Model-View-Controller)
```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor // Lombok : injection automatique
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UpdateUserResponseDTO getUser(Authentication auth) {
        // 1. Controller reçoit la requête HTTP
        // 2. Délègue au Service pour la logique métier
        // 3. Service utilise Repository pour les données
        // 4. Retour transformé en JSON automatiquement
    }
    
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UpdateUserResponseDTO> getAllUsers() {
        // Endpoint réservé aux administrateurs
    }
}
```

# Flux de Données
```bash
1. Requête HTTP → Controller
2. Controller → Validation DTO (Lombok)
3. Controller → Vérification rôle (@PreAuthorize)
4. Controller → Service (logique métier)
5. Service → Repository (base de données)
6. Repository → Service (données)
7. Service → Controller (entité)
8. Controller → DTO Response (Lombok)
9. Réponse JSON ← Controller
```

# Mapping URL avec Rôles
```bash
# Endpoints Publics
POST /api/auth/register
POST /api/auth/login
GET  /api/users/search/{userName}

# Endpoints Utilisateur Authentifié
GET    /api/users/me
PUT    /api/users/me
DELETE /api/users/me
GET    /api/posts/feed
POST   /api/posts
PUT    /api/posts/{postId}
DELETE /api/posts/{postId}
POST   /api/comments/post/{postId}
PUT    /api/comments/{commentId}
DELETE /api/comments/{commentId}

# Endpoints Admin Uniquement
GET    /api/users/admin/all
DELETE /api/users/admin/{userId}
DELETE /api/posts/admin/{postId}
DELETE /api/comments/admin/{commentId}
```

# Démarrage de l'application
```bash
# Via Maven Wrapper
./mvnw spring-boot:run

# Ou via JAR
./mvnw clean package
java -jar target/FishOn-0.0.1-SNAPSHOT.jar
```

## Compte Admin par Défaut
- __Username__ : `admin`
- __Email__ : `admin@fishon.com`
- __Mot de passe__ : `Admin123!`
- __Rôle__ : Administrateur

## Données de Test
L'application initialise automatiquement :
- 10 utilisateurs de démonstration
- 30 publications de pêche
- 60 commentaires
- 1 compte administrateur

# Avantages Lombok
## Avant Lombok
```java
public class UserModel {
    private String userName;
    private String email;
    
    public UserModel() {}
    
    public UserModel(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @Override
    public String toString() { ... }
    @Override
    public boolean equals(Object o) { ... }
    @Override
    public int hashCode() { ... }
}
```

## Après Lombok
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {
    private String userName;
    private String email;
}
```

__Réduction__ : 30+ lignes → 6 lignes (83% de réduction)

# Système d'Administration Détaillé

## Autorisation Multi-Niveaux
```java
// UserService - Contrôle des permissions
public void deleteComment(UUID commentId, UUID userId) throws CommentNotFound, UnauthorizedAccess {
    var comment = commentRepository.findById(commentId).orElseThrow(...);
    var user = userRepository.findById(userId).orElseThrow(...);
    
    // Vérification : propriétaire OU admin
    if (!comment.getUser().getId().equals(userId) && !user.isAdmin()) {
        throw new UnauthorizedAccess();
    }
    
    commentRepository.delete(comment);
}

// Méthode admin bypass
public void deleteCommentByAdmin(UUID commentId) throws CommentNotFound {
    var comment = commentRepository.findById(commentId).orElseThrow(...);
    commentRepository.delete(comment); // Pas de vérification propriété
}
```

## Différenciation Frontend
```javascript
// Exemple utilisation côté frontend
if (user.isAdmin) {
    // Afficher boutons modération
    showDeleteButton(post);
    showAdminPanel();
} else {
    // Afficher seulement ses propres contenus
    if (post.userId === user.id) {
        showEditButton(post);
    }
}
```