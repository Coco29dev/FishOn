package Universite;

public class Main {
    
    public static void main(String[] args) {
        // Création de l'université (démonstration de l'agrégation)
        Universite universite = new Universite("Université de Rennes");
        
        // Création des étudiants
        Etudiant etudiant1 = new Etudiant(1, "Dupont", "Jean");
        Etudiant etudiant2 = new Etudiant(2, "Martin", "Sophie");
        Etudiant etudiant3 = new Etudiant(3, "Petit", "Marie");
        
        // Création des cours
        Cours cours1 = new Cours("Java Avancé");
        Cours cours2 = new Cours("Web Development");
        Cours cours3 = new Cours("Algorithmes");
        
        // Démonstration de l'agrégation - ajout d'étudiants et cours à l'université
        universite.ajouterEtudiant(etudiant1);
        universite.ajouterEtudiant(etudiant2);
        universite.ajouterEtudiant(etudiant3);
        
        universite.ajouterCours(cours1);
        universite.ajouterCours(cours2);
        universite.ajouterCours(cours3);
        
        // Affichage des infos de l'université
        universite.afficherInfo();
        
        // Démonstration de la relation many-to-many entre Etudiant et Cours
        System.out.println("\n=== Inscriptions aux cours ===");
        etudiant1.inscrisptionCours(cours1);
        etudiant1.inscrisptionCours(cours2);
        
        etudiant2.inscrisptionCours(cours1);
        etudiant2.inscrisptionCours(cours3);
        
        etudiant3.inscrisptionCours(cours2);
        etudiant3.inscrisptionCours(cours3);
        
        // Affichage des infos des cours
        System.out.println("\n=== Informations sur les cours ===");
        cours1.afficherInfoCours();
        cours2.afficherInfoCours();
        cours3.afficherInfoCours();
        
        // Démonstration de la désinscription
        System.out.println("\n=== Démonstration de désinscription ===");
        etudiant1.desinscriptionCours(cours1);
        System.out.println("Après désinscription de " + etudiant1.getNom() + " du cours " + cours1.getTitre() + ":");
        cours1.afficherInfoCours();
        
        // Démonstration de la suppression d'un étudiant de l'université
        System.out.println("\n=== Retrait d'un étudiant de l'université ===");
        universite.retirerEtudiant(etudiant3);
        universite.afficherInfo();
        
        // Démonstration que l'étudiant existe toujours après retrait (agrégation)
        System.out.println("\nL'étudiant " + etudiant3.getNom() + " existe toujours après retrait de l'université");
        System.out.println("Nom: " + etudiant3.getNom() + ", Prénom: " + etudiant3.getPrenom() + ", ID: " + etudiant3.getId());
    }
}
