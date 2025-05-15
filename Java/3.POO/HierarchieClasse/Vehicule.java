package POO.HierarchieClasse;

public abstract class Vehicule {

    // Attribut privée
    private String marque;
    private String modele;
    private String couleur;

    // Constructeur paramétré
    public Vehicule(String marque, String modele, String couleur) {
        this.marque = marque;
        this.modele = modele;
        this.couleur = couleur;
    }

    // Getters
    public String getMarque() {
        return marque;
    }

    public String getModele() {
        return modele;
    }

    public String getCouleur() {
        return couleur;
    }

    // Setters
    public void setMarque(String marque) {
        if (marque != null && !marque.isEmpty()) {
            this.marque = marque;
        }
    }

    public void setModele(String modele) {
        if (modele != null && !modele.isEmpty()) {
            this.modele = modele;
        }
    }

    public void setCouleur(String couleur) {
        if (couleur != null && !couleur.isEmpty()) {
            this.couleur = couleur;
        }
    }

    // Méthodes abstraites
    public abstract void start();
    public abstract void stop();
}
