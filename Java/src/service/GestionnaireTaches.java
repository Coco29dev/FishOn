package src.service;

import src.modele.Tache;
import src.modele.Categorie;

public interface GestionnaireTaches {
    // Méthode d'interface
    // Méthode création nouvelle tâche
    public Tache nouvelleTache(String titre, String description, Categorie categorie);

    // Méthode afficher toutes les tâches
    public Tache[] afficherTaches();

    // Méthode tâche terminé
    public boolean tacheTermine(int indice);

    // Méthode afficher tâche terminé
    public Tache[] afficherTachesTermine();

    // Méthode tâche pas terminé
    public Tache[] afficherTachesNonTermine();
}
