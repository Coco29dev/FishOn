package com.FishOn.FishOn.Config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.PostRepository;

@Component // Cette classe est un bean gestion automatique de Spring
public class PostDataInitializer {

    @Autowired // Injection automatique
    private PostRepository postRepository;

    public List<PostModel> createPosts(List<UserModel> users) {
        List<PostModel> posts = new ArrayList<>();

        // ==== CRÉATION TEMPLATES ====
        String[] titles = {
                "Belle prise du matin", // 0
                "Record personnel battu", // 1
                "Sortie exceptionnelle", // 2
                "Combat épique", // 3
                "Magnifique spécimen", // 4
                "Pêche nocturne réussie", // 5
                "Première de l'année", // 6
                "Week-end parfait", // 7
                "Session productive", // 8
                "Technique payante" // 9     
        };

        String[] descriptions = {
                "Quelle journée ! Le poisson était au rendez-vous.", // 0
                "Après 2h de combat, j'ai enfin réussi à la sortir.", // 1
                "Conditions parfaites, eau claire et temps idéal.", // 2
                "Une technique qui porte enfin ses fruits !", // 3
                "Spot secret qui ne déçoit jamais.", // 4
                "Lever à 5h du matin, mais ça valait le coup !", // 5
                "Premier poisson de cette taille pour moi.", // 6
                "Belle bataille, respect pour ce combattant.", // 7
                "Technique à la mouche qui fonctionne à merveille.", // 8
                "Patience récompensée après 3h d'attente." // 9
        };

        String[] fishNames = {
                "Carpe", // 0
                "Brochet", // 1
                "Truite", // 2
                "Sandre", // 3
                "Perche", // 4
                "Black-bass", // 5
                "Gardon", // 6
                "Tanche", // 7
                "Silure", // 8
                "Barbeau" // 9
        };

        String[] locations = {
                "Lac d'Annecy", // 0
                "Seine à Paris", // 1
                "Lac du Bourget", // 2
                "Étang privé de Sologne", // 3
                "Rivière Dordogne", // 4
                "Canal du Midi", // 5
                "Lac de Sainte-Croix", // 6
                "Rhône à Lyon", // 7
                "Étang communal local", // 8
                "Réservoir de Vouglans" // 9
        };

        // ==== CRÉATION PUBLICATION ====
        Integer i = 0;
        for (UserModel user : users) {
            for (Integer j = 0; j < 3; j++) {
                // Calcul index templates
                Integer index_title = (i + j) % 10;
                Integer index_description = (i * 2 + j) % 10;
                Integer index_fishName = (i + j * 2) % 10;
                Integer index_location = (i * 3 + j) % 10;

                // Récupération données depuis les templates
                String title = titles[index_title];
                String description = descriptions[index_description];
                String fishName = fishNames[index_fishName];
                String location = locations[index_location];

                // Création publication avec données aléatoires
                PostModel post = postRepository.save(new PostModel(title, description, fishName));
                post.setUser(user);

                // Données optionnelles pour certains posts seulement
                if (j == 0 || j == 2) {
                    post.setWeight(1.0 + (i + j) * 0.5);
                    post.setLength(20.0 + (i + j) * 3.0);
                    post.setLocation(location);
                    post.setCatchDate(LocalDateTime.now().minusDays(i + j + 1));
                }
                posts.add(post);
            }
            i++;
        }
        return posts;
    }
}