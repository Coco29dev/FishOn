package VaribalesTypes.ConvTypes;

import java.time.LocalDate;
// Obtenir input utilisateur
import java.util.Scanner;

public class Main {

    // Entry point
    public static void main(String[] args) {
        // Création objet Scanner pour récupération argument CLI
        Scanner inUser = new Scanner(System.in);

        // Instrutction pour user
        System.out.println("Écrire date sous ce format : jj/mm/aaaa");

        // Lecture inputUser
        String userDate = inUser.nextLine();

        // Conversion CLI en objet LocalDate
        LocalDate date = Conv.convertStringToDate(userDate);

        if (date == null) {
            System.out.println("Impossible de convertir cette date");
        } else {
            System.out.println(date);
        }

        // Fermeture Scanner
        inUser.close();
    }
}
