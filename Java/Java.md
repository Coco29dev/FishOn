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

`final` mot-clés rendant la variable immuable.
```java
final int n = 28;
```

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

## Array