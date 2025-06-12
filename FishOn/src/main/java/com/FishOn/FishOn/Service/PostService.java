package com.FishOn.FishOn.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.PostRepository;
import com.FishOn.FishOn.Repository.UserRepository;
import com.FishOn.FishOn.Exception.FishOnException.*;

import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    // ========= Méthode CRUD =========
    public PostModel createPost(UUID userId, PostModel post)
    throws UserNotFoundById, MissingTitleException, MissingDescriptionException, MissingFishNameException {
        // Récupérer l'utilisateur avec son ID, si non trouvé envoie l'exception UserNotFound avec orElseThrow
        UserModel existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        // Validation champs obligatoire
        String title = post.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new MissingTitleException(title);
        }

        String description = post.getDescription();
        if (description == null || description.trim().isEmpty()) {
            throw new MissingDescriptionException(description);
        }

        String fishName = post.getFishName();
        if (fishName == null || fishName.trim().isEmpty()) {
            throw new MissingFishNameException(fishName);
        }

        post.setUser(existingUser);
        return postRepository.save(post);
    }
}