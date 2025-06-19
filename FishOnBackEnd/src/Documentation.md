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
__Architecture en couches__(layered architecture) respectant les principes __SOLID__ et la séparation des responsabilités.

```bash
┌─────────────────────────────────────────┐
│           COUCHE CONTROLLERS            │
│        (Présentation / API REST)        │
│                                         │
│  • AuthController                       │
│  • UserController                       │
│  • PostController                       │
│  • CommentController                    │
│                                         │
│  Responsabilités :                      │
│  - Exposer les endpoints REST           │
│  - Gérer les requêtes/réponses HTTP     │
│  - Valider les données d'entrée         │
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
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            COUCHE SERVICES              │
│           (Logique métier)              │
│                                         │
│  • AuthService                          │
│  • UserService                          │
│  • PostService                          │
│  • CommentService                       │
│                                         │
│  Responsabilités :                      │
│  - Implémenter la logique business      │
│  - Valider les règles métier            │
│  - Implémentation CRUD                  │
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
│  • UserModel                            │
│  • PostModel                            │
│  • CommentModel                         │
│                                         │
│  Responsabilités :                      │
│  - Représenter les tables en base       │
│  - Définir les relations entre entités  │
│  - Gérer les contraintes de données     │
└─────────────────────────────────────────┘
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

# spring-boot-starter-security
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
__Apporte__ :
- __Authentification/Autorisation__: Système de sécurité complet.
- `BCrypt`: Encodage sécurisé des mots de passe.
- __Session Management__: Gestion des __sessions `HTTP`__.
- `CORS/CSRF`: Protection contre les attaques web.

__Utilisé pour__:
- __Protéger__ les endpoints (authentification requise).
- __Gérer__ les sessions utilisateur.
- __Encoder__ les mots de passe.
- __Configuration `CORS`__ pour le frontend React.

# spring-boot-starter-data-jpa
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

__Apporte__:
- `Spring Data JPA`: Repositories automatiques.
- `Hibernate`: ORM (Object-Relational Mapping).
- __Transactions__: Gestion automatique des transactions.
- `Query Methods`: Méthodes de requête dérivées du nom.

__Utilisé pour__:
- __Mapper__ les entités Java vers les tables de base de données.
- __Générer__ automatiquement les méthodes CRUD.
- __Créer__ des requêtes personnalisées (`findByEmail`, `findByUserName`).
- __Gérer__ les relations entre entités (`@OneToMany`, `@ManyToOne`).

## Relations Entités
- __User__ <-> __Post__: `@OneToMany` / `@ManyToOne`.
- __Post__ <-> __Comment__: `@OneToMany` / `@ManyToOne`.
- __User__ <-> __Comment__: `@OneToMany` / `@ManyToOne`.

## Auto-Configuration
`Spring Boot` configure automatiquement l'application selon les __dépendances__ présentes.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- Auto-configure : DataSource, EntityManager, Repositories -->
```

# spring-boot-starter-validation
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

__Apporte__:
- `Jakarta Bean Validation`: Validation déclarative.
- `Hibernate Validator`: Implémentation des validations.
- __Annotations__:`@NotNull`, `@Email`, `@Size`, `@Min`, `@Max`.

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
- __Connecteur `PostgreSQL`__: Communication avec la __base de données__.
- __Dialect automatique__: `Hibernate` adapte `SQL` pour `PostgreSQL`.
- __Types spécialisés__: Support des types `PostgreSQL`(`UUID`, `JSON`, etc.).

__Utilisé pour__:
- __Connecter__ l'application à la base de données `PostgreSQL`.
- __Exécuter__ les requêtes `SQL` générées par `Hibernate`.
- __Gérer__ les types de données spécifiques (`UUID` pour les identifiants)

# spring-boot-starter-test
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

__Apporte__:
- `JUnit 5`: Framework de tests unitaires.
- `Mockito`: Simulation des dépendances.
- `Spring Test`: Tests d'intégration `Spring`.
- `AssertJ` : Assertions fluides.

__Utilisé pour__:
- __Écrire__ des tests unitaires pour les services.
- __Mocker__ les repositories dans les tests.
- __Tester__ l'intégration des controllers.

# Couche Controllers
__Interface__ entre le __client `HTTP`__ et l'application.
- __Exposition__ endpoints `REST`.
- __Validation__ données d'entrée(`@Valid`).
- __Gestion__ authentification.
- __Transformer Entity__ -> `DTO` pour les réponses.

# Couche DTO
__Contrat__ d'__interface__ entre __frontend__ et __backend__.
- __Définir__ la structure des données échangées.
- __Valider__ les entrées utilisateurs.
- __Isoler__ les modèles interenes de l'`API` publique.
- __Exclure__ les données sensibles(mots de passe).

# Couche Service
__Logique Métier__
- __Implémentation__ règle business.
- __Implémentation__ CRUD.
- __Validation__ données métier.
- __Orchestration__ appels `repositories`.
- __Gestion__ transactions.

## Inversion de Contrôle(IoC) et Injection de Dépendances
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

# Couche Repositories
__Abstraction__ de l'accès aux données.
- __Création__ requête personnalisées.
- __Abstraire__ la technologie de persistance.

# Couche Models
__Représentation__ des données en __base de données__.
- __Mapper__ les tables de __base de données__.
- __Définir__ les contraintes.
- __Gestion__ relations(`@OneToMany`, `@ManyToOne`).


# Annotations stéréotype
```java
@RestController  // Composant Spring + réponses JSON automatiques
@Service        // Logique métier
@Repository     // Accès aux données
@Configuration  // Configuration Spring
@Component      // Composant générique
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

# Flux de Données
```bash
1. Requête HTTP → Controller
2. Controller → Validation DTO
3. Controller → Service (logique métier)
4. Service → Repository (base de données)
5. Repository → Service (données)
6. Service → Controller (entité)
7. Controller → DTO Response
8. Réponse JSON ← Controller
```

# Démarrage de l'application
```bash
# Via Maven Wrapper
./mvnw spring-boot:run

# Ou via JAR
./mvnw clean package
java -jar target/FishOn-0.0.1-SNAPSHOT.jar
```
