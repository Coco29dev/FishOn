package VaribalesTypes.ConvTypes;

// Classe fournissant méthode pour manipulation dates
import java.time.LocalDate;


public class Conv {

    // Méthode de classe conversion string en objet date
    public static LocalDate convertStringToDate(String date) {

        // Vérification format date valide
        if (date.length() != 10) {
            return null;
        }

        // Vérification caractère position valide
        if (date.charAt(2) != '/' || date.charAt(5) != '/') {
            return null;
        }

        // Extraction composant de la date
        String jourStr = date.substring(0, 2);
        String moisStr = date.substring(3, 5);
        String anneeStr = date.substring(6, 10);

        // Conversion string to int
        int jourInt = Integer.parseInt(jourStr);
        int moisInt = Integer.parseInt(moisStr);
        int anneeInt = Integer.parseInt(anneeStr);

        // Vérification validité valeurs
        if (jourInt < 1 || jourInt > 31) {
            return null;
        }

        if (moisInt < 1 || moisInt > 12) {
            return null;
        }

        if (anneeInt < 0) {
            return null;
        }

        // Retourne objet LocalDate
        return LocalDate.of(anneeInt, moisInt, jourInt);
    }
}