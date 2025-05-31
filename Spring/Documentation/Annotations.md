# Annotations de Configuration
```java
@SpringBootApplication  // Point d'entrée principal
@Configuration         // Classe de configuration
@ComponentScan         // Scan des composants
@EnableAutoConfiguration // Configuration automatique
@ConfigurationProperties // Propriétés de configuration
@Profile              // Profils d'environnement
```

# Annotations de Composants(Stéréotypes)
```java
// Hiérarchie d'héritage
@Component              // Annotation parent (générique)
├── @Service           // Spécialisé pour la logique métier
├── @Repository        // Spécialisé pour l'accès aux données  
└── @Controller        // Spécialisé pour la présentation web
// Toutes héritent de @Component !
@RestController // API REST
```

# Annotations d'injection de Dépendances
```java
@Autowired    // Injection automatique
@Qualifier    // Spécifier le bean à injecter
@Value        // Injection de valeurs
@Bean         // Création de bean
@Primary      // Bean prioritaire
```

# Annotations Web MVC
```java
@RequestMapping   // Mapping d'URL
@GetMapping      // GET HTTP
@PostMapping     // POST HTTP
@PutMapping      // PUT HTTP
@DeleteMapping   // DELETE HTTP
@PathVariable    // Variable d'URL
@RequestParam    // Paramètre de requête
@RequestBody     // Corps de requête
@ResponseBody    // Corps de réponse
```

# Annotations de Validation
```java
@Valid           // Validation JSR-303
@NotNull         // Non null
@NotBlank        // Non vide
@Size            // Taille
@Email           // Format email
@Pattern         // Expression régulière
```

# Annotations JPA/Base de Données
```java
@Entity          // Entité JPA
@Table           // Table de base
@Id              // Clé primaire
@GeneratedValue  // Génération automatique
@Column          // Colonne
@OneToMany       // Relation 1-N
@ManyToOne       // Relation N-1
```

# Annotatiions de Sécurité
```java
@EnableWebSecurity     // Activer la sécurité
@PreAuthorize         // Autorisation avant méthode
@PostAuthorize        // Autorisation après méthode
@Secured              // Sécurisation simple
@RolesAllowed         // Rôles autorisés
```
