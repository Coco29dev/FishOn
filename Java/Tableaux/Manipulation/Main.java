package Tableaux.Manipulation;

import java.util.Arrays;

public class Main {

    // Entry Point
    public static void main(String[] args) {

        // Création tableaux
        int[] tableau1 = { 16, 47, 8 };
        int[] tableau2 = { 11, 24, 89 };

        // Affichage tableau avant fusion
        System.out.println(Arrays.toString(tableau1));
        System.out.println(Arrays.toString(tableau2));

        // Affichage tableau après fusion
        System.out.println(Arrays.toString(Fusion.fusionTableau(tableau1, tableau2)));
    }
}
