# FishOn
__Réseau Social__ dédié aux passionnés de __pêche__, permettant aux __utilisateurs__ de __partager__ leur prises, __commenter__ les publications et __gérer__ leur profil.

# Stack Technique
- __Framework__: `Spring Boot 3.5.0`.
- __Langage__: `Java21`.
- __Base de données__: `PostgreSQL`.
- __Sécurité__: `Spring Security` avec __session `HTTP`__.
- __Architecture__: `REST API` avec pattern `MVC`.
- __Build__: `Maven`.

# Architecture
__Architecture en couches__(layered architecture) qui sépare clairement les responsabilités.

```bash
┌─────────────────┐
│   Controllers   │ ← Couche de présentation (API REST)
├─────────────────┤
│    Services     │ ← Logique métier
├─────────────────┤
│  Repositories   │ ← Accès aux données
├─────────────────┤
│     Models      │ ← Entités JPA
└─────────────────┘
```

# Structure packages
```bash
com.FishOn.FishOn/
├── Config/          # Configuration Spring Security
├── Controller/      # API REST endpoints
├── DTO/            # Data Transfer Objects
├── Exception/      # Gestion d'erreurs centralisée
├── Model/          # Entités JPA
├── Repository/     # Accès aux données
└── Service/        # Logique métier
```

# Inversion de Contrôle(IoC) et Injection de Dépendances
`Spring Boot` utilise le __conteneur IoC__ pour gérer le __cycle de vie__ des composants.

```java
@Service
public class UserService {
    @Autowired  // Injection automatique
    private UserRepository userRepository;
}
```

- __Couplage faible__ entre les composants.
- __Facilite__ les tests.
- __Gestion__ automatique __cycle de vie__.

# Annotations stéréotype
```java
@RestController  // Composant Spring + réponses JSON automatiques
@Service        // Logique métier
@Repository     // Accès aux données
@Configuration  // Configuration Spring
@Component      // Composant générique
```

# Auto-Configuration
`Spring Boot` configure automatiquement l'application selon les __dépendances__ présentes.

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- Auto-configure : DataSource, EntityManager, Repositories -->
```

# Pattern MVC(Model-View-Controller)
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/me")
    public UpdateUserResponseDTO getUser(Authentication auth) {
        // 1. Controller reçoit la requête HTTP
        // 2. Délègue au Service pour la logique métier
        // 3. Service utilise Repository pour les données
        // 4. Retour transformé en JSON automatiquement
    }
}
```

# Mapping URL
![Mapping URL](MappingURL.png "MappingURL")

# Relations Entités
- __User__ <-> __Post__: `@OneToMany` / `@ManyToOne`.
- __Post__ <-> __Comment__: `@OneToMany` / `@ManyToOne`.
- __User__ <-> __Comment__: `@OneToMany` / `@ManyToOne`.

# Spring Security
__Authentification par session HTTP__
- __Login__: Vérification identifiants → Création session.
- __Requêtes suivantes__: Cookie de session vérifié automatiquement.
- __Logout__: Invalidation de la session.

__Protection des endpoints__
- __Publics__: /api/auth/**, /api/users/search/**
- __Protégés__: Tous les autres nécessitent une authentification.

# Gestion d'erreurs centralisée
```java
@ControllerAdvice
public class FishOnExceptionHandler {
    
    @ExceptionHandler(UserNotFoundById.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundById e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}
```
`Exception Handler` global pour trnaformer les __exceptions métier__ en réponse `HTTP` appropriées.

# Démarrage de l'application
```bash
# Via Maven Wrapper
./mvnw spring-boot:run

# Ou via JAR
./mvnw clean package
java -jar target/FishOn-0.0.1-SNAPSHOT.jar
```
