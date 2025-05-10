package src.stockage;

import src.modele.Tache;

public interface StockageTaches {
    // Méthode d'interface
    // Méthode ajout d'une tâche
    public void addTache(Tache tache);
    // Méthode récupération de toutes les tâches
    public Tache[] allTache();
    // Méthode récupération tâche avec son indice
    public Tache getByIndice(int indice);
    //Méthode MAJ une tâche
    public boolean majTache(int indice, Tache tache);
    // Méthode obtenir nb de tâche
    public int totalTache();
}
