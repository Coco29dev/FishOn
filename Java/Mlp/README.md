# Mlp
Programme qui génère une table de multiplication personalisée.

## Code
```java
package Mlp;

public class Mlp {
    public static void main(String[] args) {

        int nbDebut = 6;
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
        genMul(2, 8);
    }
    
    // Fonction génération table de multiplication
    public static void genMul(int nb, int mul) {
        for (int i = 1; i <= mul; i++) {
            int sum = nb * i;
            System.out.println(nb + "x" + i + "= " + sum);
        }
    }
}
```

## Résultat
```bash
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

2x1= 2
2x2= 4
2x3= 6
2x4= 8
2x5= 10
2x6= 12
2x7= 14
2x8= 16
```
