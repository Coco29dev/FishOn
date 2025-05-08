# Programmation Orientée Objet
__Paradigme de programmation__ consitant en la _définition_ et l'_interraction_ de briques logicielles(__objet__).
Possède une _structure interne_ et un _comportement_.
S'agit de représenter ces __objets__ et leur _relations_.

# Classe
"_Plan_" ou modèle qui définit les __attributs et les méthodes communs__ à tous les objets d'un certain __type__.
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
Voiture maVoiture = new Voiture();
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
- `protected`: Accesible dans la __classe__ et __sous-classe__.
- (default): Accesible dans le même package