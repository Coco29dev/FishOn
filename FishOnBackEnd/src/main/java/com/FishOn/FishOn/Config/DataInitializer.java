package com.FishOn.FishOn.Config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    @Autowired // Injection Automatique
    private UserDataInitializer userDataInitializer;

    @Autowired // Injection Automatique
    private PostDataInitializer postDataInitializer;

    @Autowired // Injection Automatique
    private CommentDataInitializer commentDataInitializer;

    @Autowired // Injection Automatique
    private UserRepository userRepository;

    @PostConstruct
    public void initializeData() {

        // Vérification données existante
        if (userRepository.count() > 0) {
            System.out.println("Données déjà existantes, initialisation ignorée!");
            return;
        }

        System.out.println("Initialisation des données!");

        // ---- ORCHESTRATION ----
        // Création des utilisateurs
        List<UserModel> users = userDataInitializer.createUsers();
        System.out.println(users.size() + " utilisateurs créés");

        // Création des publications
        List<PostModel> posts = postDataInitializer.createPosts(users);
        System.out.println(posts.size() + " publications créés");

        // Création des commentaires
        List<CommentModel> comments = commentDataInitializer.createComments(posts, users);
        System.out.println(comments.size() + " commentaires créés");

        System.out.println("Initialisation terminée!");
    }
}