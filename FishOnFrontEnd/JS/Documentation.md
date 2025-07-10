# Architecture Modulaire
Basée sur des __classes__ `JavaScript ES6`, avec une séparation claire des responsabilités et une approche orientée services pour appels `API`.

## Structure Générale
```bash
FishOnFrontEnd/JS/
├── API.js          # Couche d'abstraction pour les appels HTTP
├── APIService.js   # Services métier (authentification, posts, profil)
├── ErrorHandler.js # Gestion centralisée des erreurs
├── Utils.js        # Fonctions utilitaires partagées
├── Auth.js         # Gestion de l'authentification
├── Feed.js         # Page fil d'actualité
├── Journal.js      # Page journal personnel
└── Profile.js      # Page profil utilisateur
```

# Couche API(`API.js`)
- __Rôle__: Abstraction bas niveau pour les appels `HTTP`.
- __Pattern__: Classe statique avec méthodes génériques.
- __Responsabilités__:
    - Configuration de base (`URL`, `headers`, `credentials`).
    - Gestion des __codes d'erreur `HTTP`__.
    - __Méthodes `HTTP`__ standardisées(`GET`, `POST`, `PUT`, `DELETE`).

# Couche Service(`APIService.js`)
- __Rôle__: __Logique métier__ et orchestration des __appels `API`__.
- __Pattern__: __Classe statique__ avec méthodes spécialisées par domaine.
- __Responsabilités__:
    - Validation des données d'entrée.
    - Transformation des réponses `API`.
    - Gestion des erreurs métier.

# Authentification Basée sur les Cookies
```javascript
credentials: 'include' // Envoi automatique des cookies de session
```

# Gestion des Sessions Expirées
- Détection automatique des __erreurs `401`__.
- Redirection transparente vers la page de connexion.
- Messages utilisateurs informatifs

# Template `HTML`
- Utilisation de `<template>` `HTML5` pour définir des structures.
- Clonage et injection dynamique des `templates`.
- Séparation claire entre structure(`HTML`) et logique(`JS`).

# Authentification(`Auth.js`)
- 1. Validation côté client des champs obligatoires.
- 2. Appel `APIService.register`(`formData`).
- 3. Gestion du retour(succès -> redirection, erreur -> message).
- 4. Nettoyage formulaire

## Gestion formulaire
- Prévention rechargement de page(`e.preventDefault()`).
- Extraction automatique des données de formulaire.
- Validation en temps réel.

# Fil d'actualiuté(`Feed.js`)
__Architecture Modulaire__:
```javascript
// Initialisation
setupNavigation() → loadFeed() → displayPosts()
                                      ↓
              createPostCard() → setupComments() → setupCommentForm()
```

__Gestion des Commentaires__:
- __Toggle de Visibilité__: Accordéon pour afficher/masquer.
- __Formulaire Dynamique__: Apparition/disparition selon l'état.
- __Soumission Asynchrone__: Ajout sans rechargement de page.

# Journal Personnel(`Journal.js`)
__Gestion CRUD Complète__:
- __Create__: Formulaire de création avec toggle.
- __Read__: Affichage différencié
- __Update__: Modal d'édition pré-remplissage.
- __Delete__: Modal de confirmation.

## Modal Dynamiques
- 1. Clonage template.
- 2. Pré-remplisage des données.
- 3. Configuration des événements.
- 4. Injection dans le `DOM`.
- 5. Gestion de la fermeture.

# Profil Utilisateur(`Profile.js`)
```javascript
class Profile {
    static async init() {
        // Orchestration du chargement
    }
    
    static setupNavigation() {
        // Configuration des boutons
    }
    
    static async loadUserProfile() {
        // Chargement des données utilisateur
    }
}
```

# Utilitaires et Helpers(`Utils.js`)
__Résolution Automatique des Chemins__:
- 1. Chemin backend (`profilePicture`/, `fishPicture/`).
- 2. `URL` complète (`http://`, `https://`)
- 3. Chemin relatif frontend(`../IMG/`)
- 4. Image par défaut en cas d'erreur

__Modal d'affichage__:
- Création dynamique d'overlay.
- Fermeture par clic ou touche `Escape`.
- Responsive et accessibilité.

# Gestion d'Erreurs Centralisé(`ErrorHandler.js`)
__Gestion par Contexte__:
- `auth`: Erreurs d'authentification.
- `feed`: Erreurs du fil d'actualité.
- `profile`: Erreurs de profil.
- `comment`: Erreurs de commentaires.

# Pattern de Performance
`Lazy Loading`:
- Chargement des données uniquement quand nécessaire.
- Auto-refresh du feed toutes les 30 secondes.

__Optimisation `DOM`__:
- Utilisation de `DocumentFragment` via les templates.
- Manipulation `DOM` minimale(__clonage puis injection__).
- Gestion d'état pour éviter les __reflows__.

__Reflows__: Processus coûteux dans le __navigateur web__ où le __moteur de rendu__ recalcule la position et la taille des __éléments `HTML`__.

__Gestion Mémoire__:
- Nettoyage des event listeners dans les modals.
- Réutilisation des templates plutôt que création dynamique.

# Sécurité
__Validation des Données__:
```javascript
// Validation systématique côté client
if (!registerData.email || !registerData.password || !registerData.userName) {
    return {success: false, error: 'Tous les champs sont obligatoires'};
}
```

__Gestion des Sessions__:
- Envoi automatique des `cookies` d'authentification.
- Redirection automatique en cas de __session expirée__
- Messages d'erreur contextualisés sans exposition d'informations sensibles.

__Sanitisation__:
- Utilisation `.trim()` pour nettoyer les entrées utilisateur.
- Validation des types de données(`parseInt`, `parseFloat`).

# Évoluvité et Maintenance 
__Modulation__:
- Séparation claire des responsabilités.
- Classes réutilisables et extensibles.
- `API` uniformisée entre les composants.

__Configuration Centralisée__
```javascript
const API_BASE = 'http://localhost:8080/api';
// Point unique de configuration pour tous les appels API
```

__Gestion d'État Prévisible__:
- Format de retour standardisé pour tous les services.
- Gestion d'erreur cohérente dans toute l'application.
- Patterns répétables pour les nouvelles fonctionnalités.