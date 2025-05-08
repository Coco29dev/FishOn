# Mlp
Programme qui génère une table de multiplication personalisée.

## Code
```java
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
```

## Résultat
```bash
3x1 = 3
3x2 = 6
3x3 = 9
3x4 = 12
3x5 = 15

4x1 = 4
4x2 = 8
4x3 = 12
4x4 = 16
20 est un multiple de 10

5x1 = 5
10 est un multiple de 10
5x3 = 15
20 est un multiple de 10
5x5 = 25

6x1 = 6
6x2 = 12
6x3 = 18
6x4 = 24
30 est un multiple de 10

7x1 = 7
7x2 = 14
7x3 = 21
7x4 = 28
7x5 = 35
```