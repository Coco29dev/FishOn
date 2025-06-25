package com.FishOn.FishOn.Config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.PostRepository;

@Component
public class PostDataInitializer {

    @Autowired
    private PostRepository postRepository;

    public List<PostModel> createPosts(List<UserModel> users) {
        List<PostModel> posts = new ArrayList<>();

        // ==== CRÉATION TEMPLATES ====
        String[] titles = {
                "Belle prise du matin", "Record personnel battu", "Sortie exceptionnelle",
                "Combat épique", "Magnifique spécimen", "Pêche nocturne réussie",
                "Première de l'année", "Week-end parfait", "Session productive", "Technique payante"
        };

        String[] descriptions = {
                "Quelle journée ! Le poisson était au rendez-vous.",
                "Après 2h de combat, j'ai enfin réussi à la sortir.",
                "Conditions parfaites, eau claire et temps idéal.",
                "Une technique qui porte enfin ses fruits !",
                "Spot secret qui ne déçoit jamais.",
                "Lever à 5h du matin, mais ça valait le coup !",
                "Premier poisson de cette taille pour moi.",
                "Belle bataille, respect pour ce combattant.",
                "Technique à la mouche qui fonctionne à merveille.",
                "Patience récompensée après 3h d'attente."
        };

        String[] fishNames = {
                "Carpe", "Brochet", "Truite", "Sandre", "Perche",
                "Black-bass", "Gardon", "Tanche", "Silure", "Barbeau"
        };

        String[] locations = {
                "Lac d'Annecy", "Seine à Paris", "Lac du Bourget", "Étang privé de Sologne",
                "Rivière Dordogne", "Canal du Midi", "Lac de Sainte-Croix", "Rhône à Lyon",
                "Étang communal local", "Réservoir de Vouglans"
        };

        String[] photoUrls = {
                "img/fish1.jpg", "img/fish2.jpg", "img/fish3.jpg", "img/fish4.jpg", "img/fish5.jpg",
                "img/fish6.jpg", "img/fish7.jpg", "img/fish8.jpg", "img/fish9.jpg", "img/fish10.jpg"
        };

        // ==== CRÉATION PUBLICATIONS ====
        Integer i = 0;
        for (UserModel user : users) {
            for (Integer j = 0; j < 3; j++) {
                try {
                    // Calcul des index
                    Integer index_title = (i + j) % 10;
                    Integer index_description = (i * 2 + j) % 10;
                    Integer index_fishName = (i + j * 2) % 10;
                    Integer index_location = (i * 3 + j) % 10;
                    Integer index_photoUrl = (i + j) % 10;

                    // Récupération des données
                    String title = titles[index_title];
                    String description = descriptions[index_description];
                    String fishName = fishNames[index_fishName];
                    String location = locations[index_location];
                    String photoUrl = photoUrls[index_photoUrl];

                    // CORRECTION : Créer le post SANS le sauvegarder immédiatement
                    PostModel post = new PostModel(title, description, fishName, photoUrl);

                    // Définir l'utilisateur AVANT la sauvegarde
                    post.setUser(user);

                    // Données optionnelles pour certains posts seulement
                    if (j == 0 || j == 2) {
                        post.setWeight(1.0 + (i + j) * 0.5);
                        post.setLength(20.0 + (i + j) * 3.0);
                        post.setLocation(location);
                        post.setCatchDate(LocalDateTime.now().minusDays(i + j + 1));
                    }

                    // Sauvegarder APRÈS avoir défini toutes les propriétés
                    PostModel savedPost = postRepository.save(post);
                    posts.add(savedPost);

                } catch (Exception e) {
                    System.err.println("Erreur lors de la création du post pour l'utilisateur " + user.getUserName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            i++;
        }

        System.out.println("Création terminée : " + posts.size() + " posts créés");
        return posts;
    }
}