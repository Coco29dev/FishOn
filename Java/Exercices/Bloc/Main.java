package Exercices.Bloc;

public class Main {  // Classe principale qui contient le main

    public static void main(String[] args) {
        // Création d'un objet Bloc
        // Instanciation classe Bloc
        Bloc bloc = new Bloc(10, 20, 30);
        
        // Accès aux attributs via les getters (car les attributs sont privés)
        System.out.println("Longueur: " + bloc.getLongueur() + 
                           ", Largeur: " + bloc.getLargeur() + 
                           ", Hauteur: " + bloc.getHauteur());
        
        // Calcul et affichage du volume
        int volume = bloc.getLongueur() * bloc.getLargeur() * bloc.getHauteur();
        System.out.println("Volume du bloc: " + volume);
    }
}
