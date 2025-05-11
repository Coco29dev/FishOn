package src.stockage;

import src.modele.Tache;
public class StockageTableau implements StockageTaches {
    // Attributs
    private Tache[] tableauTache; // Stockage taches
    private int nbTaches; // Compteur tâches

    // Constructeur
    public StockageTableau() {
        // Initilaisation tableau taille fixe
        this.tableauTache = new Tache[10];
        this.nbTaches = 0;
    }

    // Implémentation méthode addTache()
    @Override
    public void addTache(Tache nouvelleTache) {
        // Vérification stockage tableau valide
        if (this.nbTaches < this.tableauTache.length) {
            // Stockage nouvelle tache à l'indice nbTaches
            tableauTache[nbTaches] = nouvelleTache;
            // Incrémentation nbTaches création nouvelle indice pour tableauTache
            nbTaches++;
        } else {
            System.out.println("Stockage plein");
        }
    }

    // Implémentation méthode allTache()
    @Override
    public Tache[] allTache() {
        // Création nouveauTableau, instanciation de la classe Tache
        Tache[] nouveauTableau = new Tache[nbTaches];
        // Boucle qui copie tableauTache vers nouveauTableau
        for (int i = 0; i < nbTaches; i++) {
            nouveauTableau[i] = tableauTache[i];
        }
        return nouveauTableau;
    }

    // Implémentation méthode getByIndice()
    @Override
    public Tache getByIndice(int indice) {
        // Vérification que l'indice est valide
        if (indice >= 0 && indice < nbTaches) {
            // Si valide, retourne la tâche à cet indice
            return tableauTache[indice];
        } else {
            // Si invalide, affiche un message et retourne null
            System.out.println("Indice invalide");
            return null;
        }
    }

    // Implémentation méthode majTache
    @Override
    public boolean majTache(int indice, Tache nouvelleTache) {
        // Vérification que l'indice est valide
        if (indice >= 0 && indice < nbTaches) {
            // Si valide, remplace la tâche à cet indice
            tableauTache[indice] = nouvelleTache;
            return true;
        } else {
            // Si invalide, affiche un message et retourne false
            System.out.println("Indice invalide");
            return false;
        }
    }

    // Implémentation méthode totalTache
    @Override
    public int totalTache() {
        return nbTaches;
    }
}
