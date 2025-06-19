# Vue d'ensemble
__Entités__ `JPA` qui représentent la __structure de données__ de `FishOn`.

Ces __modèles__ définissent les __tables de base de données__ et leurs __relations__ à travers un `mapping objet-relationnel(ORM)`.

# Architecture Modèles
```bash
BaseModel (classe abstraite)
├── UserModel
├── PostModel
└── CommentModel
```

# BaseModel - Classe Mère Abstraite
__Pattern__ de __classe de base__ commune pour éviter la __duplication de code__.

__Annotations clés__:
- `@MappedSuperclass`: Indique que cette classe ne sera pas une __entité__ à part entière mais que ses __attributs__ seront hérités par les __classes filles__.
- `@Id`: Désigne la __clé primaire__.
- `@GeneratedValue(strategy = GenerationType.UUID)`: Génération automatique d'identifiants `UUID`.
- `@CreationTimestamp` et `@UpdateTimestamp`: Gestion automatique des horodatages par `Hibernate`.

# UserModel - Entité Utilisateur
- Profil utilisateur complet
- Relations avec les _posts_ et _commentaires_

__Annotations de validation__:
- `@Email`: Validation du format email.
- `@Column(unique = true)`: Contrainte d'unicité sur username et email.
- `@JsonIgnore`: Exclusion du mot de passe lors de la sérialisation `JSON`.

__Relations__:
- `@OneToMany` avec `PostModel`: Un utilisateur peut avoir plusieurs posts.
- `@OneToMany` avec `CommentModel`: Un utilisateur peut écrire plusieurs commentaires.
- `cascade = CascadeType.ALL`: Les opérations sur l'utilisateur se propagent à ses entités liées(Ex: Si utilisateur supprimé ses posts/comentaires le sont aussi).
- `orphanRemoval = true`: Suppression automatique des entités orphelines.

# PostModel - Entité Publication
- Données spécifiques à la pêche (nom du poisson, poids, longueur, lieu, date de capture).
- Contenu flexible avec titre et description.

__Relations__:
- `@ManyToOne` avec `UserModel`: Plusieurs posts appartiennent à un __utilisateur__.
- `@OneToMany` avec `CommentModel`: Un __post__ peut avoir plusieurs __commentaires__.
- `@JsonIgnore` sur `user`: Évite les __boucles infinies__ lors de la __sérialisation__.

# CommentModel - Entité Commentaire
- Structure minimaliste avec contenu textuel
- Relations bidirectionnelles avec User et Post

__Relations__:
- `@ManyToOne` avec `PostModel`: Plusieurs commentaires sur un post.
- `@ManyToOne` avec `UserModel`: Plusieurs commentaires par utilisateur.

# Concepts JPA/Hibernate Utilisés
__Annotations de Persistance__:
- `@Entity`: Marque une classe comme entité persistante.
- `@Table(name = "...")`: Spécifie le nom de la table en base.
- `@Column`: Configuration des colonnes (nullable, length, unique).
- `@JoinColumn`: Définit les clés étrangères pour les relations.

__Gestion des Relations__:
- `@OneToMany`: Relation un-vers-plusieurs.
- `@ManyToOne` : Relation plusieurs-vers-un.
- `mappedBy`: Indique le côté propriétaire de la relation.
- `CascadeType.ALL`: Propagation de toutes les opérations.
- `orphanRemoval`: Suppression des entités orphelines.

__Stratégies de Génération__:
- `GenerationType.UUID`: Utilisation d'`UUID` pour les identifiants.
- `@CreationTimestamp/@UpdateTimestamp`: Gestion automatique des timestamps par `Hibernate`.

# Starters Spring Boot Impliqués
`Spring Data JPA`
- __Fonction__: Simplifie l'accès aux données avec `JPA/Hibernate`.
- __Apporte__: `Repositories` automatiques, requêtes dérivées, gestion des transactions.
- __Configuration__: Mapping automatique des entités vers les tables.

`Spring Boot Validation`
- __Fonction__: Validation des données avec Bean Validation.
- __Apporte__: Annotations de validation (`@Email`, `@NotNull`, etc.).
- __Intégration__: Validation automatique dans les controllers avec `@Valid`.

`PostgreSQL Driver`
- __Fonction__: Connecteur pour base de données `PostgreSQL`.
- __Configuration__: Dialect automatique, optimisations spécifiques `PostgreSQL`.
