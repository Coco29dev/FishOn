# Vue d'ensemble
Constitue l'__interface d'accès aux données__ de l'application.

Implémentation __pattern__ `Repository` et utilise `Spring Data JPA` pour automatiser les opérations `CRUD` et __requête personnalisées__.

# Architecture Repository
Chaque `Repository` corresponf à une entité métier et héroite de `JpaRepository<>`.

```bash
Repository
├── UserRepository
├── PostRepository
└── CommentRepository
```

# Pattern Repository
Le __pattern__ `Repository` encapsule la __logique d'accès aux données__ et centralise les __requêtes__, créant une __couche d'abstraction__ entre la __logique métier__ et la __couche de persistance__.

__Avantages__:
- Séparation des préoccupations
- Testabilité améliorée (mockage facile)
- Maintenance centralisée des requêtes
- Abstraction de la technologie de persistance

# Interface JpaRepository
__Fonctionnalités héritées automatiquement__:
- `save(T entity)`: Sauvegarde/mise à jour.
- `findById(ID id)`: Recherche par ID.
- `findAll()`: Récupération de toutes les entités.
- `deleteById(ID id)`: Suppression par ID.
- `count()`: Comptage des entités.
- `existsById(ID id)`: Vérification d'existence.

# UserRepository - Gestion des Utilisateurs
__Authentification et Sécurité__:
```jav
javaOptional<UserModel> findByUserName(String userName);
Optional<UserModel> findByEmail(String email);
```

__Utilisation__ `Optional`:
- Évite les `NullPointerException`.
- Force la gestion explicite des cas "utilisateur non trouvé".
- __Pattern__ recommandé pour les recherches uniques.

__Validation d'Unicité__:
```java
javaboolean existsByUserName(String userName);
boolean existsByEmail(String email);
```

__Cas d'usage__:
- Validation lors de l'inscription.
- Vérification avant modification de profil.
- Optimisation : retourne boolean sans charger l'entité complète.

__Filtrage par Statut__:
```java
javaList<UserModel> findByEnabledTrue();
```

__Convention Spring Data__: `findByPropertyTrue()` génère `WHERE property = true`.

# PostRepository - Gestion des Publications
__Recherche par Relation__:
```java
List<PostModel> findByUserId(UUID id);
```

__Mécanisme__: `Spring Data JPA` navigue automatiquement dans la relation `@ManyToOne` vers `UserModel`.

__Recherche par Critères Métier__:
```java
List<PostModel> findByFishName(String fishName);
List<PostModel> findByLocation(String location);
```

# CommentRepository - Gestion des Commentaires
__Relations bidirectionnelle__:
```java
javaList<CommentModel> findByUserId(UUID id);
List<CommentModel> findByPostId(UUID id);
```

- Affichage des commentaires d'un post.
- Historique des commentaires d'un utilisateur.
- Modération de contenu.
