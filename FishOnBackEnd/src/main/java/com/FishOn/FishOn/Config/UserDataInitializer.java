package com.FishOn.FishOn.Config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.UserRepository;

@Component // Cette classe est un bean gestion automatique de Spring
public class UserDataInitializer {

    @Autowired // Injection automatique
    private UserRepository userRepository;

    @Autowired // Injection automatique
    private PasswordEncoder passwordEncoder;

    public List<UserModel> createUsers() {
        List<UserModel> users = new ArrayList<>();

        // Tableau de tableaux d'objects
        // Chaque ligne {} = un utilisateur
        Object[][] userData = {
            {"capitaine_crochet", "capitaine@fishon.com", "Capitaine", "Crochet", 35, "Crochet50", "profilePicture/capitaine.jpg"},
            {"Riton", "riton@fishon.com", "Riton", "Manivelle", 66, "Riton50", "profilePicture/riton.jpg"},
            {"Kevin", "kevin@fishon.com", "Kevin", "Cadillac", 16, "Kevin50", "profilePicture/kevin.jpeg"},
            {"little_mermaid", "mermaid@fishon.com", "Little", "Mermaid", 23, "Mermaid50", "profilePicture/mermaid.jpeg"},
            {"Gaspard", "gaspard@fishon.com", "Gaspard", "Quoi", 39, "Gaspard50", "profilePicture/gaspard.jpeg"},
            {"Bigornaud", "bigornaud@fishon.com", "Mac", "Bernick", 11, "Bigornaud50", "profilePicture/bigornaud.jpeg"},
            {"la_cindy", "cindy@fishon.com", "La", "Cindy", 27, "Cindy50", "profilePicture/cindy.jpeg"},
            {"petit_poisson", "petit@fishon.com", "Petit", "Poisson", 17, "Petit50", "profilePicture/petitpoisson.jpeg"},
            {"gégé", "gégé@fishon.com", "Gégé", "Depardieu", 69, "Gégé50", "profilePicture/gégé.jpeg"},
            {"jeanne_au_secours", "jeanne@fishon.com", "Jeanne", "Tipiak", 77, "Jeanne50", "profilePicture/jeanne.jpeg"}
        };

        // Boucle For-Each création utilisateurs
        // Pour chaque ligne data dans le tableau userData
        for (Object[] data : userData) {
            // Instanciation nouvel utilisateur et sauvegarde en base de données
            UserModel user = userRepository.save(new UserModel(
                (String) /* Conversion forcée vers String */ data[0], // username
                (String) /* Conversion forcée vers String */ data[1], // email
                (String) /* Conversion forcée vers String */ data[2], // firstName
                (String) /* Conversion forcée vers String */ data[3], // lastName
                (Integer) /* Conversion forcée vers Integer */ data[4],// age
                passwordEncoder.encode((String) /* Conversion forcée vers String */ data[5]), // password
                (String) data[6] // profilePicture
            ));
            users.add(user);
        }
        return users;
    }
}