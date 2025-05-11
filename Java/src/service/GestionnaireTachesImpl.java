package src.service;

import src.modele.Categorie;
import src.modele.Tache;
import src.stockage.StockageTaches;

public class GestionnaireTachesImpl implements GestionnaireTaches {

    // Attribut pour le stockage (injectable)
    private StockageTaches stockageTaches;

    // Construteur avec injection de dépendance
    public GestionnaireTachesImpl(StockageTaches stockageTaches) {
        this.stockageTaches = stockageTaches;
    }

    // Implémentation méthode nouvelleTaches
    @Override
    public Tache nouvelleTache(String titre, String description, Categorie categorie) {

        // Création nouvelle tache via instanciation classe Tache
        Tache nouvelleTache = new Tache(titre, description, categorie);

        // Ajouter au stockage
        stockageTaches.addTache(nouvelleTache);

        return nouvelleTache;
    }

    // Implémentation méthode afficherTaches()
    @Override
    public Tache[] afficherTaches() {
        return stockageTaches.allTaches();
    }

    // Implémentation méthode tâche terminée
    @Override
    public boolean tacheTermine(int indice) {

        Tache tache = stockageTaches.getByIndice(indice);

        // Vérification tâche existante
        if (tache == null) {
            return false;
        }

        // Apppel méthode pour passer à l'état true
        tache.tacheTermine();

        // MAJ de la tâche dans le stockage
        return stockageTaches.majTache(indice, tache);
    }

    // Implémentation méthode afficherTachesTermine()
    @Override
    public Tache[] afficherTachesTermine() {

        // Récupération toutes les tâches
        Tache taches = stockageTaches.allTache();

        // Comptage nb de tâches terminées
        int nbTachesTermine = 0;
        for (Tache tache : taches) {
            if (tache.isTermine()) {
                nbTachesTermine++;
            }
        }

        // Création tableau pour tâche terminée
        Tache[] tachesTermine = new Tache[nbTachesTermine];

        // Remplissage tableau
        int i = 0;
        for (Tache tache : taches) {
            if (tache.isTermine()) {
                tachesTermine[i] = tache;
                i++;
            }
        }

        return tachesTermine;
    }

    // Implémentation méthode afficherTachesNonTermine()
    @Override
    public Tache[] afficherTachesNonTermine() {

        // Récupération toutes les tâches
        Tache taches = stockageTaches.allTache();

        // Comptage tâches pas terminées
        int nbTachesNonTermine = 0;
        for (Tache tache : taches) {
            if (!tache.isTermine()) {
                nbTachesNonTermine++;
            }
        }

        // Création tableau tâches non terminées
        Tache [] tachesNonTermine = new Tache[nbTachesNonTermine];

        // Remplissage tableau
        int i = 0;
        for (Tache tache : taches) {
            if (!tache.isTermine()) {
                tacheNonTermine[i] = tache;
                i++;
            }
        }

        return tachesNonTermine;
    }
}