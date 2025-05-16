# Système de Gestion Universitaire

Ce projet implémente un système de gestion universitaire simple en Java, démontrant deux types fondamentaux de relations entre classes : **l'agrégation** et les relations **many-to-many**.

## Structure du Projet

Le projet est organisé en quatre classes principales :

- `Universite.java` : Représente une université qui contient des étudiants et des cours
- `Etudiant.java` : Représente un étudiant qui peut s'inscrire à plusieurs cours
- `Cours.java` : Représente un cours auquel plusieurs étudiants peuvent s'inscrire
- `Main.java` : Programme principal qui démontre l'utilisation des classes

## Concepts Implémentés

### Agrégation

L'agrégation est un type de relation entre classes où une classe "contient" des références à d'autres classes, mais ces objets peuvent exister indépendamment. Dans notre projet :

- L'université agrège des étudiants et des cours
- Si l'université est supprimée, les étudiants et les cours continuent d'exister
- Un étudiant ou un cours peut appartenir à plusieurs universités

Cette relation est implémentée dans la classe `Universite` à travers les méthodes :

```java
public boolean ajouterEtudiant(Etudiant etudiant)
public boolean retirerEtudiant(Etudiant etudiant)
public boolean ajouterCours(Cours cours)
public boolean retirerCours(Cours cours)
```

### Relation Many-to-Many

Une relation many-to-many permet à plusieurs objets d'une classe d'être associés à plusieurs objets d'une autre classe. Dans notre projet :

- Un étudiant peut s'inscrire à plusieurs cours
- Un cours peut avoir plusieurs étudiants inscrits

Cette relation est implémentée de manière bidirectionnelle dans les classes `Etudiant` et `Cours` :

```java
// Dans Etudiant.java
public void inscrisptionCours(Cours cours)
public void desinscriptionCours(Cours cours)

// Dans Cours.java
public void ajouterEtudiant(Etudiant etudiant)
public void retirerEtudiant(Etudiant etudiant)
```

Chaque méthode met à jour à la fois l'objet courant et l'objet associé, tout en évitant les boucles infinies grâce à des vérifications appropriées.

## Encapsulation et Protection des Données

Le projet démontre également de bonnes pratiques d'encapsulation :

- Tous les attributs sont privés
- Des getters et setters appropriés sont fournis
- Les listes retournées sont des copies pour éviter la modification directe
- Des vérifications de validation sont effectuées avant d'ajouter ou retirer des éléments

## Exécution du Programme

Le programme principal (`Main.java`) démontre :

1. La création d'une université, d'étudiants et de cours
2. L'ajout d'étudiants et de cours à l'université (agrégation)
3. L'inscription des étudiants à différents cours (relation many-to-many)
4. La désinscription d'un étudiant d'un cours
5. Le retrait d'un étudiant de l'université
6. La persistance de l'objet étudiant après le retrait (démontrant l'agrégation)

Pour exécuter le programme :

```bash
javac Universite/*.java
java Universite.Main
```

## Sortie Attendue

```
Liste des étudiants de l'université Université de Rennes:
Nombres d'étudiants: 3
Nombres de cours offerts: 3

=== Inscriptions aux cours ===

=== Informations sur les cours ===
Cours: Java Avancé
Nombre d'étudiants inscrits: 2
Cours: Web Development
Nombre d'étudiants inscrits: 2
Cours: Algorithmes
Nombre d'étudiants inscrits: 2

=== Démonstration de désinscription ===
Après désinscription de Dupont du cours Java Avancé:
Cours: Java Avancé
Nombre d'étudiants inscrits: 1

=== Retrait d'un étudiant de l'université ===
Liste des étudiants de l'université Université de Rennes:
Nombres d'étudiants: 2
Nombres de cours offerts: 3

L'étudiant Petit existe toujours après retrait de l'université
Nom: Petit, Prénom: Marie, ID: 3
```

## Extensions Possibles

Ce projet pourrait être étendu de plusieurs façons :

1. Ajouter plus d'attributs aux classes (crédits pour les cours, département pour les étudiants, etc.)
2. Implémenter la persistance des données avec une base de données
3. Ajouter une interface utilisateur graphique
4. Implémenter des fonctionnalités avancées comme la génération d'emploi du temps
5. Ajouter d'autres classes comme Professeur, Département, etc.

## Conclusion

Ce projet démontre comment implémenter et utiliser correctement les relations d'agrégation et many-to-many en Java, en suivant les principes de la programmation orientée objet et de l'encapsulation.