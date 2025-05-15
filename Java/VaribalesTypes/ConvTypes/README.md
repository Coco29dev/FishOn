# Convertisseur de Date
Ce projet illustre un exemple simple de conversion de chaînes de caractères en objets date en Java, en utilisant les fonctionnalités modernes de l'`API java.time`.

## Structure du projet
Le projet contient deux classes principales :

- `Conv.java` : Classe utilitaire qui fournit des méthodes de conversion de date
- `Main.java` : Point d'entrée du programme avec interface utilisateur simple

## Fonctionnalités
- Conversion d'une chaîne au format "jj/mm/aaaa" en objet LocalDate
- Validation du format de la date
- Vérification de la validité des valeurs (jour, mois, année)
- Interface utilisateur simple en ligne de commande

## Concepts mis en œuvre

### Variables et types
- Utilisation de différents types de données (String, int, LocalDate)
- Conversion entre types (String vers int)
- Manipulation d'objets immutables (LocalDate)

### Structures conditionnelles et méthodes
- Utilisation de conditions if/else pour la validation
- Méthodes statiques pour la séparation des responsabilités

### Programmation Orientée Objet
- Organisation du code en classes avec des responsabilités distinctes
- Utilisation d'APIs Java modernes (java.time.LocalDate)

### Classe Conv
- La classe Conv contient la méthode `convertStringToDate` qui :

- Vérifie que la chaîne a une longueur de 10 caractères
- Vérifie que les séparateurs '/' sont aux bonnes positions (2 et 5)
- Extrait les composants jour, mois et année
- Convertit ces composants en entiers
- Vérifie que les valeurs sont dans des intervalles valides
- Crée et retourne un objet LocalDate, ou null si la conversion échoue

```java
public static LocalDate convertStringToDate(String date) {
    // Vérification format date valide
    if (date.length() != 10) {
        return null;
    }
    
    // Vérification caractère position valide
    if (date.charAt(2) != '/' || date.charAt(5) != '/') {
        return null;
    }
    
    // Extraction et validation des composants...
    
    // Retourne objet LocalDate
    return LocalDate.of(anneeInt, moisInt, jourInt);
}
```

## Classe Main
La classe Main contient la méthode `main` qui :

- Crée un objet Scanner pour lire l'entrée utilisateur
- Demande à l'utilisateur d'entrer une date
- Appelle la méthode de conversion depuis la classe Conv
- Affiche le résultat ou un message d'erreur
- Ferme proprement les ressources (Scanner)

## Comment exécuter le programme
Compilez les deux classes :
```bash
javac ExVaribalesTypes/ConvTypes/Conv.java ExVaribalesTypes/ConvTypes/Main.java
```
Exécutez la classe Main :
```bash
java ExVaribalesTypes.ConvTypes.Main
```
Suivez les instructions à l'écran pour entrer une date au format "jj/mm/aaaa"

## Exemples d'utilisation
- Entrée valide : "25/12/2025" → Affiche "2025-12-25"
- Format invalide : "25-12-2025" → Affiche "Impossible de convertir cette date"
- Valeurs invalides : "32/13/2025" → Affiche "Impossible de convertir cette date"

## Notes techniques
- L'implémentation utilise `java.time.LocalDate` qui est l'`API moderne` recommandée depuis `Java 8`
- La méthode `LocalDate.of()` effectue automatiquement certaines validations (comme vérifier si le 29 février existe dans une année bissextile)
- La classe String offre des méthodes utiles comme `substring()` et `charAt()` pour manipuler les chaînes
