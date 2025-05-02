# Univers Java
Langage de __programmation haut niveau orienté objet__. Créer par __James Gosling__ et __Patrick Naugthon__ employés de __Sun Microsystems__. Présenté officiellement le 23 mai 1995 au _SunWorld_.

Langage __compilé__: _Code source_ est compilé par un _compilateur_ afin de produire un _code exécutable_.

_Code source_: Instrcutions programme rédigées dans un la,gage de programmation crée et maîtrisé par l'homme.
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

`final` mot-clé rendant la variable immuable.
```java
final int n = 28;
```

`var` mot-clé permettant subtistution au _type_ lors de déclaration de variable.
```java
var entier = 5;
```
L'utilisatioon de ce _mot-clé_ est à faire avec précaution. Dans le __bytecode__ généré, le __compilateur__ aura défini le bon type à la place de `var`, il fut donc que la variable sois initialisé pour que le __compilateur__ détermine le _type_.

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


## Fonctions
