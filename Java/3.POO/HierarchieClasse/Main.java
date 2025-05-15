package HierarchieClasse;

public class Main {
    
    public static void main(String[] args) {
        // Création d'instances de Moto
        Moto moto1 = new Moto("Yamaha", "MT-07", "Noir", "Roadster");
        Moto moto2 = new Moto("Ducati", "Panigale V4", "Rouge", "Sportive");
        
        // Démonstration des getters
        System.out.println("=== Informations sur les motos ===");
        System.out.println("Moto 1 : " + moto1.getMarque() + " " + moto1.getModele() + ", " + moto1.getCouleur() + ", Catégorie : " + moto1.getCategorie());
        System.out.println("Moto 2 : " + moto2.getMarque() + " " + moto2.getModele() + ", " + moto2.getCouleur() + ", Catégorie : " + moto2.getCategorie());
        
        // Démonstration des setters
        System.out.println("\n=== Modification des attributs ===");
        moto1.setCouleur("Bleu");
        moto2.setCategorie("Hypersport");
        System.out.println("Nouvelle couleur de la moto 1 : " + moto1.getCouleur());
        System.out.println("Nouvelle catégorie de la moto 2 : " + moto2.getCategorie());
        
        // Démonstration du polymorphisme
        System.out.println("\n=== Démonstration du polymorphisme ===");
        
        // Tableau de véhicules
        Vehicule[] vehicules = new Vehicule[2];
        vehicules[0] = moto1;
        vehicules[1] = moto2;
        
        // Appel des méthodes polymorphes
        System.out.println("Démarrage des véhicules :");
        for (Vehicule vehicule : vehicules) {
            System.out.print(vehicule.getMarque() + " " + vehicule.getModele() + " : ");
            vehicule.start();
        }
        
        System.out.println("\nArrêt des véhicules :");
        for (Vehicule vehicule : vehicules) {
            System.out.print(vehicule.getMarque() + " " + vehicule.getModele() + " : ");
            vehicule.stop();
        }
    }
}
