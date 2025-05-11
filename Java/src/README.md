# Gestionnaire de tâches
Créer un gestionnaire de tâches simple qui permet de créer, afficher et marquer des tâches comme terminées.

## Concepts
- Variables et types
- Tableaux (arrays)
- Structures conditionnelles et itératives
- POO (classes et objets)
- Relations entre classes
- Interfaces
- Réduction du couplage via l'injection de dépendances

## Categorie.java
Créez une __énumération__ avec différentes catégories de tâches(`ETUDE`, `PERSONNEL`).

## Tache.java
Créez une classe pour représenter une tâche avec :

- Des __attributs__ pour le _titre_, la _description_, la _catégorie_ et si la tâche est terminée.
- Un __constructeur__ approprié.
- Des __getters__ et __setters__.
- Une __méthode__ pour marquer la tâche comme terminée.
- Une __méthode__ `toString()` pour afficher les informations de la tâche.

## StockageTaches.java
Créez une interface qui définit les méthodes pour :

- Ajouter une tâche.
- Récupérer toutes les tâches.
- Récupérer une tâche par son indice.
- Mettre à jour une tâche.
- Obtenir le nombre total de tâches.

## StockageTableau.java
Implémentez l'interface StockageTaches en utilisant un tableau simple (pas de collections) :

- Utilisez un tableau de Tache avec une taille fixe (ex: 10).
- Gardez une variable pour suivre le nombre de tâches actuellement stockées.
- Implémentez toutes les méthodes définies dans l'interface.

## GestionnaireTaches.java
Créez une interface qui définit les méthodes pour :

- Créer une nouvelle tâche.
- Afficher toutes les tâches.
- Marquer une tâche comme terminée.
- Afficher les tâches terminées.
- Afficher les tâches non terminées.

## GestionnaireTachesImpl.java
Implémentez l'interface GestionnaireTaches :

- Utilisez l'injection de dépendances pour recevoir une instance de StockageTaches.
- Implémentez toutes les méthodes définies dans l'interface.
