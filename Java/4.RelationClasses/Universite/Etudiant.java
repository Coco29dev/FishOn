package Universite;

import java.util.LinkedList;

public class Etudiant {

    // Attributs
    private int id;
    private String nom;
    private String prenom;
    private LinkedList<Cours> coursInscrit;

    // Constructeur
    public Etudiant(int id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.coursInscrit = new LinkedList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public LinkedList<Cours> listeCours() {
        LinkedList<Cours> copyListe = coursInscrit.clone();
        return coursInscrit;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void inscrisptionCours(Cours cours) {
        if (!coursInscrit.contains(cours)) {
            coursInscrit.add(cours);

            // Éviter boucle infini
            if (!cours.listeEtudiant().contains(this)) {
                cours.ajouterEtudiant(this);
            }
        }
    }

    public void desinscriptionCours(Cours cours) {
        if (coursInscrit.contains(cours)) {
            coursInscrit.remove(cours);

            // Éviter boucle infini
            if (cours.listeEtudiant().contains(this)) {
                cours.retirerEtudiant(this);
            }
        }
    }
}
