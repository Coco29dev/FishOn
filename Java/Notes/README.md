# Notes
Définition méthode de calcul du moyenne.

## Calcul.java
```java
package Notes;

public class Calcul {
    public static double moyenne(double[] notes) {
        // Somme des 3 notes
        double somme = notes[0] + notes[1] + notes[2];
        // Calcul de la moyenne
        double moyenne = somme / 3;
        return moyenne;
    }
}
```

### Review
`public class Calcul`: Classe accessible depuis d'autres classes, même en dehors du package.

`public static double moyenne(double[] notes)`:
- `public`: Utilisé par d'autres classes.
- `static`: Appelée sans créer d'objet `Calcul`.
- `double`: Retourne nb à virgule. Utilisation fréquente pour calculer moyenne, plus précis que `float`.
- `double[] notes`: Paramètres un tableau de _type_ `double`.


## Main.java
```java
package Notes;

public class Main {
    public static void main(String[] args) {
        double[] notes = { 15.5, 8.3, 19.7 };
        double resultat = Calcul.moyenne(notes);
        System.out.println("La moyenne des notes est : " + resultat);
    }
}
```

### Review
`public class Main`: Point d'entrée programme.

`public static void main(String[] args)`: Méthode principal
- `public`: acessible de partout.
- `static`: Appelé sans créer d'objet `Main`.
- `void`: ne retourne rien.
- `String[] args`: Paramètres passés en __CLI__.