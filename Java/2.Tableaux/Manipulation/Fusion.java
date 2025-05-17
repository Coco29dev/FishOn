package Tableaux.Manipulation;

import java.util.Arrays;

public class Fusion {

    // Méthode fusion deux tableaux, obtenir un seul tableau trié
    public static int[] fusionTableau(int[] tableau1, int[] tableau2) {

        // Vérification tableau valide
        if (tableau1 == null || tableau2 == null) {
            System.out.println("Il manque un des deux tableau pour utiliser la fonction.");
            return null;
        }

        // Longueur tableaux
        int t1 = tableau1.length;
        int t2 = tableau2.length;

        // Vérification tableau non vide
        if (t1 == 0 || t2 == 0) {
            System.out.println("L'un des deux tableaux est vide.");
            return null;
        }

        // Taille tableau fusionné
        int nouveauTableauTaille = t1 + t2;

        // Création nouveau tableau
        int[] nouveauTableau = new int[nouveauTableauTaille];
        System.arraycopy(tableau1, 0, nouveauTableau, 0, t1);
        System.arraycopy(tableau2, 0, nouveauTableau, t1, t2);

        Arrays.sort(nouveauTableau);

        return nouveauTableau;
    }
}
