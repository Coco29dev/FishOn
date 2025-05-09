# Programmation Orientée Objet
__Paradigme de programmation__ consitant en la _définition_ et l'_interraction_ de briques logicielles(__objet__).
Possède une _structure interne_ et un _comportement_.
S'agit de représenter ces __objets__ et leur _relations_.

# Classe
"_Plan_" ou modèle qui définit les __attributs et les méthodes communs__ à tous les objets d'un certain __type__.
- Des __attributs__(_variables_) représentant l'état.
- Des __méthodes__(_fonctions_) représentant le comportement.

```java
public class Voiture {
    // Attributs (ou variable d'instance)
    String marque;
    String modele;
    int annee;

    // Méthodes
    public void demarer() {
        System.out.println("La voiture démarre");
    }

    public void arreter() {
        System.out.println("La voiture s'arrête");
    }
}
```

# Objet
__Instance d'une classe__ qui _encapsule_ les __données__(_attributs_) et __comportements__(_méthodes_).

__Caractéristique__:
- Possède un __type__(détermine son _comportement_).
- Possède __identité unique__(_adresse mémoire_).
- Possède une __valeur__.
- Peut avoir __attributs et méthodes__.

__Instance__: Réalisation concrète d'une __classe__. Si la __classe__ est le "_plan_", __l'instance__ est la "_maison_" construite selon ce plan.

```java
// Instancitation 
Voiture maVoiture = new Voiture();
// Objet appelant méthode de classe
maVoiture.marque = "Renault";
maVoiture.modele = "C15";
maVoiture.annee = "1907";
maVoiture.demarrer();
```

# Encapsulation
Consiste à masquer les _détails internes_ d'un __objet__ et à exposer uniquement ce qui est nécessaire.

__Qualificateur de visibilité__:
- `public`: Accessible de partout.
- `private`: Accesible uniquement dans la __classe__.
- `protected`: Accesible dans la __classe__ et __sous-classe__(__Héritage__ appliqué sur cette __classe__).
- (default): Accesible dans le même _package_

```java
public class compteBancaire {
    // Attribut privé - Accessible uniquement dans la classe
    private double solde;

    // Méthode publique pour manipuler l'attribut
    public double getSolde() {
        if (montant > 0) {
            solde += montant;
        }
    }
}
```

_Méthode_ d'accès __attribut privé__:
- `getter`: _Méthode_ qui renvoie la valeur de la variable.
- `setter`: _Méthode_ qui définit la valeur.

```java
public class Person {
    private String name;

    // Getter
    public String getName() {
        return name;
    }

    // Setter
    public void setName(String newName) {
        this.name = newName;
    }
}


# Héritage
_Mécanisme_ permettant à une __classe__ d'obtenir les _attributs_ et _méthodes_ d'une autre __classe__.

```java
// Classe parent/super-classe
public class Animal {
    protected String nom;

    // Constructeur initialise l'attribut nom
    public Animal(String nom) {
        this.nom = nom;
    }

    public void manger() {
        System.out.println(nom + " mange.");
    }
}

// Classe enfant/sous-classe
public class Chien extends Animal {
    private String race;

    // Constructeur initialise l'attribut race
    public Chien(String nom, String race) {
        // Appel constructeur classe parent
        super(nom);
        this.race = race;
    }

    public void aboyer() {
        System.out.println(nom + " aboie.");
    }

    // Redéfinition d'une méthode héritée
    @Override
    public void manger() {
        System.out.println(nom + " mange des croquettes.")
    }
}
