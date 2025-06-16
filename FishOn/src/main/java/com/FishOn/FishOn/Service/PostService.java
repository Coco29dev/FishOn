package com.FishOn.FishOn.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.PostRepository;
import com.FishOn.FishOn.Repository.UserRepository;
import com.FishOn.FishOn.Exception.FishOnException.*;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    //========= Méthode validation Post =========
    public void validateData(PostModel post) throws MissingTitleException, MissingDescriptionException, MissingFishNameException {
        // Validation titre
        String title = post.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new MissingTitleException(title);
        }

        // Validation description
        String description = post.getDescription();
        if (description == null || description.trim().isEmpty()) {
            throw new MissingDescriptionException(description);
        }

        // Validation fishname
        String fishName = post.getFishName();
        if (fishName == null || fishName.trim().isEmpty()) {
            throw new MissingFishNameException(fishName);
        }
    }

    // ========= Méthode CRUD =========
    public PostModel createPost(UUID userId, PostModel post)
            throws UserNotFoundById, MissingTitleException, MissingDescriptionException, MissingFishNameException {
        // Récupérer l'utilisateur avec son ID, si non trouvé envoie l'exception UserNotFound avec orElseThrow
        UserModel existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        // Validation Champ obligatoire
        validateData(post);

        post.setUser(existingUser);
        return postRepository.save(post);
    }
    
    public PostModel updatePost(UUID userId, UUID postId, PostModel updatedPost)
            throws UserNotFoundById, PostNotFoundById, UnauthorizedModificationPost, MissingTitleException,
            MissingDescriptionException, MissingFishNameException {
        // Récupérer l'utilisateur avec son ID, si non trouvé envoie l'exception UserNotFound avec orElseThrow
        UserModel existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        // Récupération post via ID, Si inexistant levé exception avec orElseThrow
        PostModel existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundById(postId));

        // Récupération objet User
        UserModel userPost = existingPost.getUser();

        // Vérification Utilisateur possède la publication
        if (!userPost.getId().equals(existingUser.getId())) {
            throw new UnauthorizedModificationPost();
        }

        // Validation champ obligatoire
        validateData(updatedPost);

        // MAJ champ obligatoires
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setDescription(updatedPost.getDescription());
        existingPost.setFishName(updatedPost.getFishName());

        // MAJ champs optionnels
        existingPost.setWeight(updatedPost.getWeight());
        existingPost.setLength(updatedPost.getLength());
        existingPost.setLocation(updatedPost.getLocation());
        existingPost.setCatchDate(updatedPost.getCatchDate());

        return postRepository.save(existingPost);
    }

    public void deletePost(UUID userId, UUID postId)
            throws UserNotFoundById, PostNotFoundById, UnauthorizedModificationPost {
        // Récupérer l'utilisateur avec son ID, si non trouvé envoie l'exception UserNotFound avec orElseThrow
        UserModel existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        // Récupération post via ID, Si inexistant levé exception avec orElseThrow
        PostModel existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundById(postId));

        // Récupération objet User
        UserModel userPost = existingPost.getUser();

        // Vérification Utilisateur possède la publication
        if (!userPost.getId().equals(existingUser.getId())) {
            throw new UnauthorizedModificationPost();
        }

        postRepository.delete(existingPost);
        System.out.println("Publication supprimé");
    }

    // ========= Méthode Repository =========
    public List<PostModel> getAll() {
        return postRepository.findAll();
    }

    public List<PostModel> getByUserName(String userName) throws UserNotFoundByUserName {
        if (!userRepository.existsByUserName(userName)) {
            throw new UserNotFoundByUserName(userName);
        }
        return postRepository.findByUserName(userName);
    }

    public List<PostModel> getByUserId(UUID userId) throws UserNotFoundById {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundById(userId);
        }
        return postRepository.findByUserId(userId);
    }

    public List<PostModel> getByFishName(String fishName) throws FishNameNotFound {
        if (!postRepository.existsByFishName(fishName)) {
            throw new FishNameNotFound(fishName);
        }
        return postRepository.findByFishName(fishName);
    }

    public List<PostModel> getByLocation(String location) throws LocationNotFound {
        if (!postRepository.existsByLocation(location)) {
            throw new LocationNotFound(location);
        }
        return postRepository.findByLocation(location);
    }
}