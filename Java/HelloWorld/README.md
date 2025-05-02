# Hello, World!
Tâche de notoriété publqiue dans le développment web, nous commençons par le commencement afficher cette phrase iconique.

## Code
```java
package HelloWorld;

public class HelloWorld {
    public static void main(String[] args) {
        String h = "Hello,World!";
        System.out.println(h);
    }
}
```

## Review
`package HelloWorld;`: Définition du paquet (groupe de classes).

`public class HelloWorld`: Déclaration __classe publique__.

`public static void main(String[] args)`:
- `public`: Méthode accessible de partout.
- `static`: Permet l'utilisation sans créer d'_objet_.
- `void`: Ne retourne rien.
- `main`: Méthode obligatoire.
- `String[] args`: Tableau contenant les arguments fournis depuis la __CLI__.