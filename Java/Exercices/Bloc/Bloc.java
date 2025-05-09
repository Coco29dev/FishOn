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
