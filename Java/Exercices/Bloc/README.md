# Bloc
Créez une classe nommée Bloc
    - Ajoutez un attribut longueur, un attribut largeur, un attribut hauteur. Les trois attributs seront de type int et de visibilité privée.
    - Ajoutez un constructeur paramétré permettant de définir la valeur des trois attributs.
    - Ajouter l’accesseur pour chaque attribut.

## Main.java
```java
package Exercices.Bloc;

public class Main {  // Classe principale qui contient le main

    public static void main(String[] args) {
        // Création d'un objet Bloc
        // Instanciation classe Bloc
        Bloc bloc = new Bloc(10, 20, 30);
        
        // Accès aux attributs via les getters (car les attributs sont privés)
        System.out.println("Longueur: " + bloc.getLongueur() + 
                           ", Largeur: " + bloc.getLargeur() + 
                           ", Hauteur: " + bloc.getHauteur());
        
        // Calcul et affichage du volume
        int volume = bloc.getLongueur() * bloc.getLargeur() * bloc.getHauteur();
        System.out.println("Volume du bloc: " + volume);
    }
}
```

## Bloc.java
```java
package Exercices.Bloc;

public class Bloc {
    // Attributs privés
    private int longueur;
    private int largeur;
    private int hauteur;

    // Constructeur paramétré
    public Bloc(final int longueur, final int largeur, final int hauteur) {
        this.largeur = largeur;
        this.longueur = longueur;
        this.hauteur = hauteur;
    }

    // Getters
    public int getLongueur() {
        return longueur;
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }
    
    // Méthode pour calculer le volume
    public int calculerVolume() {
        return longueur * largeur * hauteur;
    }
}
```

## Résultat
```bash
Longueur: 10, Largeur: 20, Hauteur: 30
Volume du bloc: 6000
```