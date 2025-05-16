package Universite;

import java.util.LinkedList;

public class Cours {

    // Attributs
    private String titre;
    private LinkedList<Etudiant> etudiantsInscrits;

    // Constructeur
    public Cours(String titre) {
        this.titre = titre;
        this.etudiantsInscrits = new LinkedList<>();
    }

    // Getters
    public String getTitre() {
        return titre;
    }

    public LinkedList<Etudiant> listeEtudiant() {
        LinkedList<Etudiant> copyListe = new LinkedList<>(etudiantsInscrits);
        return copyListe;
    }

    // Setter
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void ajouterEtudiant(Etudiant etudiant) {
        // Vérification étudiant déjà inscrit
        if (!etudiantsInscrits.contains(etudiant)) {
            etudiantsInscrits.add(etudiant);

            // Éviter boucle infini
            if (!etudiant.listeCours().contains(this)) {
                etudiant.inscrisptionCours(this);
            }
        }
    }

    public void retirerEtudiant(Etudiant etudiant) {
        // Vérification etudiant inscrit
        if (etudiantsInscrits.contains(etudiant)) {
            // Supprimer étudiant liste des inscrits
            etudiantsInscrits.remove(etudiant);

            // Éviter boucle infini en vérifiant si l'étudiant à ce cours
            if (etudiant.listeCours().contains(this)) {
                etudiant.desinscriptionCours(this);
            }
        }
    }

    public int nbEtudiantInscris() {
        return etudiantsInscrits.size();
    }

    public void afficherInfoCours() {
        System.out.println("Cours: " + titre);
        System.out.println("Nombre d'étudiants inscrits: " + nbEtudiantInscris());
    }
}
