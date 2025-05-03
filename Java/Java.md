# Univers Java
Langage de __programmation haut niveau orienté objet__. Créer par __James Gosling__ et __Patrick Naugthon__ employés de __Sun Microsystems__. Présenté officiellement le 23 mai 1995 au _SunWorld_.

Langage __compilé__: _Code source_ est compilé par un _compilateur_ afin de produire un _code exécutable_.

_Code source_: Instrcutions programme rédigées dans un _langage de programmation_ crée et maîtrisé par l'homme.

_Compilateur_: Programme réalisant _compilation_ code source, code spécifique et optimisé pour le _microprocesseur_.

_Code exécutable_: Suite d'octets, portion programme compilé, compréhensible par l'ordinateur et réalisant une tâche.


La _compilation_ ce fais par __Java Development Kit(JDK)__ Une fois _compilé_, on obtient du _bytecode_, ce dernier est exécutable par une __Java Virtual Machine(JVM)__, enfin l'exécution du programme compilé ce fais via __Java Runtime Environement(JRE)__.

__Java Development Kit(JDK)__ et __Java Runtime Environement(JRE)__ embarque une __Java Virtual Machine(JVM)__.

Langage __multiplateforme__, ne dépend pas d'un __OS__. Avantage de __portabilité__ des applications.

Langage __typé__: Définit le _type de données_ que l'on manipule.

Langage __Objet__: _Paradigme_ de programmation, consiste à définir et interagir avec des blocs logicielles(__objet__); Ce dernier représente un _concept_, une _idée_, possédant une _structure interne_ et un _comportement_.


## Compilation
__Création fichier.java__.
```bash
touch HelloWorld.java
```
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello,World!");
    }
// Résultat: Hello,World!
}
```
Le _nom du fichier_ doit obligatoirement être le nom de la `public class`.

__Compiler le fichier.java__.
```bash
javac HelloWorld/HelloWorld.java
```
Doit correspondre au _chemin_ dans lequel le _fichier_ se trouve
Cela va créer un fichier `HelloWorld.class`, qui contient le _bytecode_ lisible par la __JVM__.

__Exécution programme__.
```bash
java HelloWorld.HelloWorld
```


## Variables
Espace mémoire __OS__ qui contient une _donnée_.
Ce _définit_ par un _nom_ et un _type_.
```java
type variableName = value;
```

- __Déclaration__: Définition _type_ et _nom_ de la variable.
- __Affecter__: Utilisation _opérateur_ `=`.
- __Initialiser__: Insertion _donnée_.

Une variable possède une __portée__, sera utilisable uniquement dans certains _blocs de code_.
- __Portée de la méthode__: Disponible n'importe où dans la méthode.
- __Portée du bloc__: tout le code entre les `{}`.

`final` mot-clé rendant la variable immuable.
```java
final int n = 28;
```

`var` mot-clé permettant subtistution au _type_ lors de déclaration de variable.
```java
var entier = 5;
```
L'utilisatioon de ce _mot-clé_ est à faire avec précaution. Dans le __bytecode__ généré, le __compilateur__ aura défini le bon _type_ à la place de `var`, il faut donc que la variable sois initialisé pour que le __compilateur__ détermine le _type_.

### Types Primitifs
- `String`: Stockage _chaîne de caractère_. "Hello"
- `int`: Stockage des _entiers_.
- `float`: Stockage _nombres à virgule_.
- `char`: Stockage _caractère unique_. 'a'
- `boolean`: Stockage _valeur d'état_.
- `byte`: Valeur numérique de _-128_ à _127_ inclus.
- `long`: Valeur numérique de _-2e31_ à _2e31-1_ inclus.
- `short`: Valeur numérique  de _-32,768_ à _32,767_ inclus.

```java
String name = "Al";
int age = "28";
```

### Types complexes
Un __objet__ peut assembler un _ensemble de données_, ce qui permet la définition de _données complexes_ et donc de __types complexes__.
```java
String name = "Al";
String concat = "Je me nomme" + name;
```


## Tableau unidimensionnel
Stockage de plusieurs _valeurs_ dans une __même variable__.
```java
int[] array = {1, 2, 3};
```
Accès à un élément du __tableau__ en indiquant son __indice__.
```java
array[1]; // Résultat: 2
```

`new` mot-clé initilisant 3 cases dans un __tableau__.
```java
char[] array = new char[3];
array[0] = 'a';
array[1] = 'b';
array[2] = 'c';
System.out.println(array) // abc
```

Accès à la longueur du tableau via `.length`
```java
System.out.println(array.length); // 3
```


## Méthodes/Fonctions
Sous-programme qui permet d'effectuer un _ensemble d'instruction_ par simple _appel_ de cette dernière.

Doit être _déclaré_ au sein d'une __classe__.

__Java__ fournit des __méthodes/fonctions__ intégrées.

Possède :
- Une signature(_prototype_).
- Une implémentation, correspond aux _instructions_.
```java
public static void main(String[] args)
```
![Méthode](méthode.png)

### Paramètres/Arguments
Des _infromations_ peuvent être transmises aux __méthodes/fonctions__. Ses __paramètres/arguments__ agissent comme des variables au sein de la __méthode/fonction__.
```java
public static void affiche(final String texte) {
    System.out.println(texte);
}
```
La __portée__ du __paramètre/argument__ est la __méthode/focntion__. En dehors de cette dernière impossible d'y accèder.

`final` peut-être mis devant les __paramètres/arguments__, c'est une _bonne pratique_ car ils ne sont pas voué à être _modifié_.


## Récursive
__Méthode/Fonction__ s'appelant elle-même. Permet la _décomposition_ des _problèmes complexes_ en _problèmes simples_.
Cette dernière doit possèder un __critère d'arrêt__ pour éviter de _boucler indéfiniment_.
```java
public static void décompte(final int valeur) {
    if(valeur >= 0) {
        System.out.println(valeur);
        décompte(valeur-1);
    }
}
```


## Manipualtion de données
Deux point fondamentaux __structures conditionnelles__ et __structures itérative__.

Les __variables__, __fonctions__, __conditions__, __itérations__ appatiennent au __paradigme de programmation structurée__:
- Utilisation des _structures de contrôle_ pour améliorer la _clarté_, _qualité_ et le _temps_ de dévelopement d'un programme.
- Constitue un _sous-ensemble_ de la __programation impérative__.

### Opérateurs relationnels
- `a < b`: inférieur à
- `a <= b`: inférieur ou égal à
- `a > b`: Supérieur à
- `a >= b`: Supérieur ou égale à
- `a == b`: Égal à
- `a != b`: Différent de

### Opérateurs conditionnels
- `&&`: ET
- `||`: OU

### Opérateurs comparasion de type
- `instanceof`: Compare un _objet_ à un _type spécifique_

### Structures conditionnelles
_Instructions_ qui permettent de tester si une __condition__ est vraie ou non.

### L'instruction if
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

### Condition boolean
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

### Opérateur ternaire
Composé de trois _opérandes_, cela permet un _raccouci de syntaxe_.
```java
variable = (condition) ? expressionTrue : expressionFalse;

int time = 20;

String result = (time < 18) ? "GoodDay." : "GoodNigth.";

System.out.println(result); // GoodDay.
```

### Structure itérative
Exécution _bloc d'instructions_ à répétition, celle-ci _dépendant_ d'une __condition__.
