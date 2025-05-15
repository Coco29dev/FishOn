# Hiérarchie de Classes Véhicule en Java

Ce projet implémente une hiérarchie de classes pour représenter différents types de véhicules, démontrant les concepts fondamentaux de la Programmation Orientée Objet (POO) en Java.

## Structure du Projet

Le projet est organisé selon la structure suivante :
- `Vehicule.java` : Classe abstraite définissant les caractéristiques communes à tous les véhicules
- `Moto.java` : Classe concrète qui étend Vehicule avec des fonctionnalités spécifiques aux motos
- `Main.java` : Programme principal qui démontre l'utilisation de la hiérarchie de classes

## Concepts POO Implémentés

### Abstraction
- La classe `Vehicule` est définie comme abstraite, fournissant un modèle pour les types de véhicules spécifiques
- Les méthodes abstraites `start()` et `stop()` définissent un contrat que les classes filles doivent implémenter

### Encapsulation
- Tous les attributs sont déclarés comme privés pour contrôler l'accès
- Des getters et setters appropriés sont fournis pour manipuler les attributs
- Les setters incluent une validation pour assurer l'intégrité des données

### Héritage
- La classe `Moto` hérite des attributs et méthodes de la classe parente `Vehicule`
- Le constructeur de la classe fille appelle explicitement le constructeur parent à l'aide de `super()`
- Les méthodes et attributs hérités sont accessibles dans la classe fille

### Polymorphisme
- Les méthodes abstraites sont implémentées différemment dans la classe fille
- Des objets de type `Moto` peuvent être traités comme des objets de type `Vehicule`
- Un tableau de `Vehicule` peut contenir différents types de véhicules

## Fonctionnalités

### Classe Vehicule
- Attributs communs : marque, modèle, couleur
- Méthodes abstraites : start(), stop()
- Encapsulation complète avec getters et setters validés

### Classe Moto
- Attribut spécifique : catégorie
- Implémentation personnalisée des méthodes start() et stop()
- Héritage de tous les attributs et comportements de Vehicule

### Programme Principal
- Création d'instances de Moto
- Démonstration de l'utilisation des getters et setters
- Illustration du polymorphisme via un tableau de Vehicule

## Comment Exécuter le Programme

1. Compilez les fichiers sources :
   ```
   javac -d . POO/HierarchieClasse/*.java
   ```

2. Exécutez la classe principale :
   ```
   java POO.HierarchieClasse.Main
   ```

## Exemple de Sortie

```
=== Informations sur les motos ===
Moto 1 : Yamaha MT-07, Noir, Catégorie : Roadster
Moto 2 : Ducati Panigale V4, Rouge, Catégorie : Sportive

=== Modification des attributs ===
Nouvelle couleur de la moto 1 : Bleu
Nouvelle catégorie de la moto 2 : Hypersport

=== Démonstration du polymorphisme ===
Démarrage des véhicules :
Yamaha MT-07 : Le Lion rugis
Ducati Panigale V4 : Le Lion rugis

Arrêt des véhicules :
Yamaha MT-07 : Le roi est mort
Ducati Panigale V4 : Le roi est mort
```

## Extensions Possibles

Pour étendre ce projet, vous pourriez :
1. Ajouter d'autres classes concrètes comme `Voiture` et `Camion`
2. Implémenter des interfaces supplémentaires (comme `Assurable` ou `Louable`)
3. Ajouter des méthodes spécifiques à chaque type de véhicule
4. Créer une classe de gestion comme `Garage` ou `Flotte` qui manipule plusieurs véhicules

## Notes Techniques
- Le projet utilise Java standard sans dépendances externes
- La structure de package est `POO.HierarchieClasse`
- L'utilisation de méthodes abstraites garantit l'implémentation des comportements essentiels
