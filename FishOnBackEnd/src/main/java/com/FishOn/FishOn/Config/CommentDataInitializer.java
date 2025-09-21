package com.FishOn.FishOn.Config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.CommentRepository;

@Component
public class CommentDataInitializer {

    @Autowired
    private CommentRepository commentRepository;

    public List<CommentModel> createComments(List<PostModel> posts, List<UserModel> users) {
        List<CommentModel> comments = new ArrayList<>();

        // ==== CRÉATION TEMPLATES ====
        String[] commentsTemplates = {
                "Magnifique prise ! Félicitations !",                 // 0
                "Quel combat ça a dû être !",                         // 1
                "Superbe technique, bravo !",                         // 2
                "Tu peux partager ton spot ? 😉",                     // 3
                "Respect pour cette belle prise !",                   // 4
                "Ça donne envie d'y retourner !",                     // 5
                "Belle session de pêche !",                           // 6
                "Impressionnant ce spécimen !",                       // 7
                "Tu as utilisé quel appât ?",                         // 8
                "Quelle taille exactement ?",                         // 9
                "Le rêve de tout pêcheur !",                          // 10
                "Chapeau pour cette réussite !",                      // 11
                "Elle a bien lutté dis donc !",                       // 12
                "Parfait pour un barbecue !",                         // 13
                "Tu l'as remise à l'eau ?",                           // 14
                "Technique au top !",                                 // 15
                "Quelle patience il faut avoir !",                    // 16
                "Le poisson du siècle !",                             // 17
                "Bravo pour cette persévérance !",                    // 18
                "Ça c'est de la pêche !",                             // 19
                "Tu me donnes tes conseils ?",                        // 20
                "Incroyable cette prise !",                           // 21
                "Le spot de rêve !",                                  // 22
                "Tu as eu de la chance !",                            // 23
                "Superbe photo en plus !",                            // 24
                "Quel matériel tu utilises ?",                        // 25
                "Elle est magnifique !",                              // 26
                "Première fois que j'en vois une si grosse !",        // 27
                "Tu m'emmènes la prochaine fois ?",                   // 28
                "Légende vivante de la pêche !"                       // 29
        };

        // ==== CRÉATION COMMENTAIRES ====
        // Boucle principale : parcours des 30 publications
        Integer i = 0; // Index du post (0 à 29)

        for (PostModel post : posts) { // Pour chaque publication

            // Création de 2 commentaires par publication
            for (Integer j = 0; j < 2; j++) { // j = 0 (1er commentaire), j = 1 (2ème commentaire)

                // ---- CALCUL DU USER PROPRIÉTAIRE ----
                // Formule : index_post ÷ 3 = index_user_propriétaire
                // Post 0,1,2 → User 0 | Post 3,4,5 → User 1 | etc.
                Integer ownerIndex = i / 3;

                // ---- CALCUL DU USER COMMENTATEUR ----
                // Formule de base pour générer de la variété
                Integer commenterIndex = (i + j + 1) % 10;

                // ---- VÉRIFICATION ANTI-CONFLIT ----
                // Un utilisateur ne doit jamais commenter ses propres posts
                if (commenterIndex == ownerIndex) {
                    commenterIndex = (commenterIndex + 1) % 10; // Décalage sécurisé
                }

                // ---- RÉCUPÉRATION DU TEMPLATE DE COMMENTAIRE ----
                // Formule pour distribuer les 30 templates sur 60 commentaires
                Integer templateIndex = (i * 2 + j) % 30;
                String commentContent = commentsTemplates[templateIndex];

                // ---- CRÉATION ET CONFIGURATION DU COMMENTAIRE ----
                // Création avec le contenu du template
                CommentModel comment = new CommentModel(commentContent);

                // Association avec l'utilisateur commentateur
                UserModel commenter = users.get(commenterIndex);
                comment.setUser(commenter);

                // Association avec la publication commentée
                comment.setPost(post);

                // ---- PERSISTANCE ET AJOUT À LA COLLECTION ----
                // Sauvegarde en base de données via JPA
                CommentModel savedComment = commentRepository.save(comment);

                // Ajout à la liste de retour pour l'orchestrateur
                comments.add(savedComment);
            }
            i++;
        }
        return comments;
    }
}