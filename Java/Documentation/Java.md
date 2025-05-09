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
javac Exercices/Bloc/Main.java
```
Être dans le répertoire parent.
Cela va créer un fichier `Main.class`, qui contient le _bytecode_ lisible par la __JVM__.

__Exécution programme__.
```bash
java Exercices.Bloc.Main
```