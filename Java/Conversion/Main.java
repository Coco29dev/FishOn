package Conversion;

public class Main {
    public static void main(String[] args) {
        // Tableau température
        int[] celsius = { 0, 25, 37 };

        // Assignation température
        int t1 = celsius[0];
        int t2 = celsius[1];
        int t3 = celsius[2];

        // Conversion température
        double f1 = ((t1 * 1.8) + 32);
        double f2 = ((t2 * 1.8) + 32);
        double f3 = ((t3 * 1.8) + 32);

        // Affichage température
        System.out.println(t1 + "°C = " + f1 + "°F");
        System.out.println(t2 + "°C = " + f2 + "°F");
        System.out.println(t3 + "°C = " + f3 + "°F");
    }
}