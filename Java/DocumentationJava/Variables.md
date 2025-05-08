# Variables
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