# Fusion de Tableaux Triés - Exercice Java
Ce projet démontre l'implémentation d'une méthode qui fusionne deux tableaux d'entiers en un seul tableau trié.

## Structure du projet
Le projet se compose de deux classes principales :

- `Fusion.java` : Contient la méthode statique fusionTableau pour fusionner deux tableaux
- `Main.java` : Programme principal qui démontre l'utilisation de la méthode de fusion

## Fonctionnalités
- Fusion de deux tableaux d'entiers en un seul tableau trié
- Vérification de la validité des tableaux d'entrée (null et vide)
- Affichage des tableaux avant et après fusion

## Concepts mis en œuvre

### Tableaux (Arrays)
- Manipulation de tableaux d'entiers
- Utilisation de System.arraycopy pour copier des éléments entre tableaux
- Utilisation de Arrays.sort pour trier un tableau

### Structures conditionnelles
- Validation des entrées avec des conditions if/else
- Gestion des cas particuliers (tableaux null ou vides)

### Programmation modulaire
- Séparation de la logique métier (fusion) du code client (main)
- Utilisation de méthodes statiques pour des opérations utilitaires

## Classe Fusion
La classe Fusion contient la méthode fusionTableau qui :

- Vérifie si l'un des tableaux d'entrée est null
- Vérifie si l'un des tableaux d'entrée est vide
- Crée un nouveau tableau de taille combinée
- Copie les éléments des deux tableaux d'entrée dans le nouveau tableau
- Trie le nouveau tableau
- Retourne le tableau trié

```java
public static int[] fusionTableau(int[] tableau1, int[] tableau2) {
    // Vérifications et fusion...
    // Utilisation de System.arraycopy et Arrays.sort
    return nouveauTableau;
}
```

## Classe Main
La classe Main démontre l'utilisation de la méthode de fusion :

- Crée deux tableaux d'entiers
- Affiche les tableaux d'entrée
- Appelle la méthode de fusion
- Affiche le tableau résultant

## Comment exécuter le programme
Compilez les deux classes :
```bash
javac Tableaux/Manipulation/Fusion.java Tableaux/Manipulation/Main.java
```
Exécutez la classe Main :
```bash
java Tableaux.Manipulation.Main
```

### Exemple d'exécution
```bash
[16, 47, 8]
[11, 24, 89]
[8, 11, 16, 24, 47, 89]
```

## Notes techniques
- Cette implémentation utilise Arrays.sort() pour trier le tableau final, ce qui a une complexité de O(n log n).
- Une implémentation plus efficace aurait pu exploiter le fait que les tableaux d'entrée sont déjà triés, permettant une fusion en O(n).
- Pour l'exercice, nous avons choisi la simplicité de l'implémentation plutôt que l'optimisation maximale.