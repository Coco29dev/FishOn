package POO.HierarchieClasse;

public class Moto extends Vehicule {

    // Attribut privé
    private String categorie;

    // Constructeur
    public Moto(String marque, String modele, String couleur, String categorie) {
        // Appel Constructeur classe Parent
        super(marque, modele, couleur);
        this.categorie = categorie;
    }

    // Getter
    public String getCategorie() {
        return categorie;
    }

    // Setter
    public void setCategorie(String categorie) {
        if (categorie != null && !categorie.isEmpty()) {
            this.categorie = categorie;
        }
    }

    @Override
    public void start() {
        System.out.println("Le Lion rugis");
    }

    @Override
    public void stop() {
        System.out.println("Le roi est mort");
    }
}
