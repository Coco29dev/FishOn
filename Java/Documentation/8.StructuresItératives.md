## Structure itérative
Exécution _bloc d'instructions_ à répétition, celle-ci _dépendant_ d'une __condition__.

### L'instruction switch
Sélection _possibilité_ bloc de code a exécuter via le mot-clé `case`, si aucune _possibilité_, c'est l'_insrtuction_ associé à `défault` qui sera exécutée.
```java
public static void method(final String meteo) {
    switch (meteo) {
        case "soleil" -> System.out.println("Beau temps");
        case "nuage" -> System.out.println("Couvert");
        case "pluie" -> System.out.println("Mauvais temps");
        default -> System.out.println("Jsp");
    }
}
```

`break` mot-clé sortant du bloc une fois instrcution exécuté.
```java
int day = 4;
switch (day) {
    case 1 -> System.out.println("Monday");
    break;
    case 2 -> System.out.println("Tuesday");
    break;
    case 3 -> System.out.println("Wednesday");
    break;
    case 4 -> System.out.println("Thursday");
    break;
    case 5 -> System.out.println("Friday");
    break;
}
```

### Boucle While
__Boucle__ parcourant un bloc tant qu'une __condition spécifié__ est `true`.
```java
public static void print() {
    int i = 0;
    while(i < 5){
        System.out.println(i);
        i++;
    }
}
```

### Boucle Do/While
Exécution bloc de code avant vérification __condition__ est `true`, puis exécution boucle qu'une __condition spécifié__ est `true`.
```java
public static void main(String[] args) {
    int i = 1;
    do { // Ce bloc sera exécuter
        System.out.println("Compteur: " + i);
        i++;
    } while (i < 5); // Ensuite exécution de la boucle avec bloc de code

    System.out.println("Boucle terminé!");
}
```

### Boucle For
__Boucle__ parcourant bloc de code un nb de fois défini pour _exécution_ de ce dernier.
```java
public static void len() {
    for(int i = 0; i < 9, i++) {
        System;out.println(i);
    }
}
```
- `int i = 0`: Déclaration / Initialisation i.
- `i < 9`: Définition condition fin de boucle.
- `i++`: Incrémentation de i.

### Boucle  For-Each
Parcourir éléments d'un tableau(ou d'autres ensemble).
```java
String[] cars = {"Volvo", "BMW", "Ford", "Mazda"};
for (String i : cars) {
  System.out.println(i);
}
System.out.println();
/*
Volvo
BMW
Ford
Mazda
*/ 
```

### Boucles imbriquées
__Boucle interne__ exécutée à chaque itération de la __boucle externe__.
```java
public static void main(String[] args) {
    int i = 5;
    int j = 10;

    // Boucle externe
    for (int x = 0; x < i; x++) {
        // Boucle interne
        for (int z = 0; z < j; z++) {
            System.out.println("* ");
        }
        // Après chaque ligne on passe ligne suivante
        System.out.println();
    }
}
```

`continue` mot-clé iterrompant une _itération_, si une __condition spécifiée__ se produit, et continue _itération suivante_.
```java
public static void main(String[] args) {
    for(char i == 'a'; i < 'z'; i++) {
        if (i == 'e') {
            continue;
        }
        System.out.println(i);
    }
}
```
