package Générateur;

// Importation classe Array du package java.util
// Cette classe contient des méthodes de manipulation des tableaux
import java.util.Arrays;

public class Générateur {
    // Méthode static génération suite de nb pairs
    public static int[] genNbPairs(int début, int fin) {
        // Vérification nb pairs
        if (début % 2 == 1) {
            début += 1;
        }

        // Vérification nb pairs
        if (fin % 2 == 1) {
            fin -= 1;
        }

        // Calcul taille du tableau
        int taille;
        if (début <= fin) {
            taille = (fin - début) / 2 + 1;
        } else {
            taille = 0;
        }

        // Initialisation tableau à la taille exact
        int[] nbPairs = new int[taille];

        // Indice pour nbPairs[]
        int idx = 0;
        for (int i = début; i <= fin; i = i + 2) {
            // Assignation valeur actuelle a son indice
            nbPairs[idx] = i;
            // Incrémentation indice
            idx += 1;
        }

        return nbPairs;
    }

    // Méthode statique calcul somme éléments d'un tableau
    public static int sum(int[] array) {
        int somme = 0;

        // ForEach boucle
        for (int i : array) {
            // A chaque itération somme prend la valeur de i
            // Adiition valeur actuelle à la somme
            somme = somme + i;
        }

        return somme;
    }

    // Entry point
    public static void main(String[] args) {
        int[] a = genNbPairs(2, 8);
        int b = sum(a);

        // .toString affiche contenu du tableau au lieu de sa référence mémoire
        System.out.println(Arrays.toString(a));
        System.out.println(b);
    }
}
