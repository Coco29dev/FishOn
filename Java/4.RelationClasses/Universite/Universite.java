package Universite;

import java.util.LinkedList;

public class Universite {

    // Attributs
    private String nom;
    private LinkedList<Cours> listeCours;
    private LinkedList<Etudiant> etudiantInscrits;

    // Constructeur
    public Universite(String nom) {
        this.nom = nom;
        this.listeCours = new LinkedList<>();
        this.etudiantInscrits = new LinkedList<>();
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public LinkedList<Cours> getCoursOffers() {
        LinkedList<Cours> copyListeCours = new LinkedList<>(listeCours);
        return copyListeCours;
    }

    public LinkedList<Etudiant> getListeEtudiants() {
        LinkedList<Etudiant> copyListeEtudiant = new LinkedList<>(etudiantInscrits);
        return copyListeEtudiant;
    }

    // Setter
    public void setNom(String nom) {
        this.nom = nom;
    }

    // Méthodes gestion étudiants (relation d'agrégation)
    public boolean ajouterEtudiant(Etudiant etudiant) {
        // Vérification étudiants pas encore inscrits
        if (etudiant != null && !etudiantInscrits.contains(etudiant)) {
            etudiantInscrits.add(etudiant);
            return true;
        } else {
            return false;
        }
    }

    public boolean retirerEtudiant(Etudiant etudiant) {
        // Vérification étudiant inscrits
        if (etudiant != null && etudiantInscrits.contains(etudiant)) {
            etudiantInscrits.remove(etudiant);
            return true;
        } else {
            return false;
        }
    }

    public LinkedList<Etudiant> listeEtudiantInscris() {
        return getListeEtudiants();
    }

    // Méthodes gestion cours (relation d'agrégation)
    public boolean ajouterCours(Cours cours) {
        // Vérification cours pas encore ajouté
        if (cours != null && !listeCours.contains(cours)) {
            listeCours.add(cours);
            return true;
        } else {
            return false;
        }
    }

    public boolean retirerCours(Cours cours) {
        // Vérification cours existant
        if (cours != null && listeCours.contains(cours)) {
            listeCours.remove(cours);
            return true;
        } else {
            return false;
        }
    }

    public LinkedList<Cours> listeCoursOffers() {
        return getCoursOffers();
    }

    // Méthodes affichage informations
    public void afficherInfo() {
        System.out.println("Liste des étudiants de l'université " + nom + ":");
        System.out.println("Nombres d'étudiants: " + etudiantInscrits.size());
        System.out.println("Nombres de cours offerts: " + listeCours.size());
    }
}
