package Mlp;

public class Mlp {
    public static void main(String[] args) {
        
        int nbDebut = 3;
        int nbFin = 7;
        int mul = 5;

        // Boucle externe
        for (int i = nbDebut; i <= nbFin; i++) {
            // Boucle interne
            for (int j = 1; j <= mul; j++) {
                int sum = i * j;
                // Vérification multiple de 10
                if (sum % 10 == 0) {
                    System.out.println(sum + " est un multiple de 10");
                } else {
                    System.out.println(i + "x" + j + " = " + sum);
                }
            }
            // Passer à la ligne suivante
            System.out.println();
        }
    }
}
