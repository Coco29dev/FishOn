package Exercices.Notes;

public class Calcul {
    public static double moyenne(double[] notes) {
        // Somme des 3 notes
        double somme = notes[0] + notes[1] + notes[2];
        // Calcul de la moyenne
        double moyenne = somme / 3;
        return moyenne;
    }
}
