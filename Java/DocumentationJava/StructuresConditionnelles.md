# Structures conditionnelles
_Instructions_ qui permettent de tester si une __condition__ est vraie ou non.

## L'instruction if
- `if`: Spécifie un _bloc de code_ à exécuter, si une __condition__ est vrai.
- `else`: Spécifie un _bloc de code_ à exécuter, si la __même condition__ est fausse.
- `else if`: Spécifie une __nouvelle condition__ à tester, si la première __condition__ est fausse.
```java
int i = 20;

if(i > 20) {
    System.out.println(i);
} // Faux, donc ce bloc est ignoré
else if(i < 20) {
    System.out.println(i);
} // Faux, donc ce bloc est ignoré
else {
    System.out.println(i);
} // Les deux conditions sont fausses, ce bloc s'exécute
// 20
```

## Condition boolean
Utilisation des _opérateurs de comparaison_ `==` ou `!=`.
```java
public static void egal(final int value) {
    // Évalué et renvoi true ou false
    if(value == 0) {
        System.out.println("Value est égale à zéro.");
    } else {
        System.out.println("Value n'est pas égale à zéro.");
    }
}
```

Utilisation paramètre de __type boolean__.
```java
public static void meteo(final boolean beauTemps) {
    if(beauTemps) {
        System.out.println("Je vais à la plage");
    } else {
        System.out.println("Je vais au cinéma");
    }
}
```
__Java__ évalue directement le contenu, donc _opérateur de comparaison_ pas nécessaire dans ce cadre.

Utilisation de la négation `!`.
```java
public static void meteo(final boolean beau) {
    if (!beau) {
        System.out.println("Je vais à la plage");
    } else {
        System.out.println("Je vais au cinéma");
    }
}
```

Comparaison __chaîne de caractères__ `.equals`.
```java
public static void meteo(final String temps) {
    if(temps.equals("soleil")) {
        System.out.println("Je vais à la plage");
    }
}
```

## Opérateur ternaire
Composé de trois _opérandes_, cela permet un _raccouci de syntaxe_.
```java
variable = (condition) ? expressionTrue : expressionFalse;

int time = 20;

String result = (time < 18) ? "GoodDay." : "GoodNigth.";

System.out.println(result); // GoodDay.
```