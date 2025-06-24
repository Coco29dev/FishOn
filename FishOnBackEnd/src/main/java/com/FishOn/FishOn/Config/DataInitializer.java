package com.FishOn.FishOn.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.CommentRepository;
import com.FishOn.FishOn.Repository.PostRepository;
import com.FishOn.FishOn.Repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeData() {
        // Vérification données existantes
        if (userRepository.count() > 0) {
            return; // Si oui sors de la méthode
        } // Sinon création données

        // === CRÉATION UTILISATEURS ===
        UserModel user1 = userRepository.save(new UserModel(
            "capitaine_crochet",
            "capitaine@fishon.com",
            "Capitaine",
            "Crochet",
            35,
            passwordEncoder.encode("Crochet50"),
            "img/capitaine.jpg"
        ));

        UserModel user2 = userRepository.save(new UserModel(
            "Riton",
            "riton@fishon.com",
            "Riton",
            "Manivelle",
            66,
            passwordEncoder.encode("Riton50"),
            "img/riton.jpg"
        ));

        UserModel user3 = userRepository.save(new UserModel(
            "Kevin",
            "kevin@fishon.com",
            "Kevin",
            "Cadillac",
            16,
            passwordEncoder.encode("Kevin50"),
            "img/kevin.jpeg"
        ));

        UserModel user4 = userRepository.save(new UserModel(
            "little_mermaid",
            "mermaid@fishon.com",
            "Little",
            "Mermaid",
            23,
            passwordEncoder.encode("Mermaid50"),
            "img/mermaid.jpeg"
        ));

        UserModel user5 = userRepository.save(new UserModel(
            "Gaspard",
            "gaspard@fishon.com",
            "Gaspard",
            "Quoi",
            39,
            passwordEncoder.encode("Gaspard50"),
            "img/gaspard.jpeg"
        ));

        UserModel user6 = userRepository.save(new UserModel(
            "Bigornaud",
            "bigornaud@fishon.com",
            "Mac",
            "Bernick",
            11,
            passwordEncoder.encode("Bigornaud50"),
            "img/bigornaud.jpeg"
        ));

        UserModel user7 = userRepository.save(new UserModel(
            "la_cindy",
            "cindy@fishon.com",
            "La",
            "Cindy",
            27,
            passwordEncoder.encode("Cindy50"),
            "img/cindy.jpeg"
        ));

        UserModel user8 = userRepository.save(new UserModel(
            "petit_poisson",
            "petit@fishon.com",
            "Petit",
            "Poisson",
            17,
            passwordEncoder.encode("Petit50"),
            "img/petitpoisson.jpeg"
        ));

        UserModel user9 = userRepository.save(new UserModel(
            "gégé",
            "gégé@fishon.com",
            "Gégé",
            "Depardieu",
            69,
            passwordEncoder.encode("Gégé50"),
            "img/gégé.jpeg"
        ));

        UserModel user10 = userRepository.save(new UserModel(
            "jeanne_au_secours",
            "jeanne@fishon.com",
            "Jeanne",
            "Tipiak",
            77,
            passwordEncoder.encode("Jeanne50"),
            "img/jeanne.jpeg"
        ));
    }
}