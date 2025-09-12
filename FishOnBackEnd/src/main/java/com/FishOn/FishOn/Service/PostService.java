package com.FishOn.FishOn.Service;

import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.PostRepository;
import com.FishOn.FishOn.Repository.UserRepository;
import com.FishOn.FishOn.Exception.FishOnException.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des publications
 * LOMBOK UTILISÉ :
 * @RequiredArgsConstructor : Injection par constructeur automatique pour les champs final
 * @Slf4j : Logger automatique disponible via log.info(), log.error(), etc.
 */
@Service
@RequiredArgsConstructor // LOMBOK : Remplace @Autowired
@Slf4j // LOMBOK : Logger automatique
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // =============== MÉTHODE DE VALIDATION ===============

    public void validateData(PostModel post) throws MissingTitleException, MissingDescriptionException,
            MissingFishNameException, MissingPhotoException {

        // Validation titre
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            throw new MissingTitleException(post.getTitle());
        }

        // Validation description
        if (post.getDescription() == null || post.getDescription().trim().isEmpty()) {
            throw new MissingDescriptionException(post.getDescription());
        }

        // Validation fishname
        if (post.getFishName() == null || post.getFishName().trim().isEmpty()) {
            throw new MissingFishNameException(post.getFishName());
        }

        // Validation photo
        if (post.getPhotoUrl() == null || post.getPhotoUrl().trim().isEmpty()) {
            throw new MissingPhotoException(post.getPhotoUrl());
        }
    }

    // =============== MÉTHODES CRUD STANDARD ===============

    public PostModel createPost(UUID userId, PostModel post)
            throws UserNotFoundById, MissingTitleException, MissingDescriptionException,
            MissingFishNameException, MissingPhotoException {

        // Récupération utilisateur
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        // Validation données obligatoires
        validateData(post);

        // Association avec l'utilisateur
        post.setUser(existingUser);

        var savedPost = postRepository.save(post);

        log.info("Nouvelle publication créée par {} (ID: {}): {}",
                existingUser.getUserName(), existingUser.getId(), savedPost.getTitle());

        return savedPost;
    }

    public PostModel updatePost(UUID userId, UUID postId, PostModel updatedPost)
            throws UserNotFoundById, PostNotFoundById, UnauthorizedModificationPost,
            MissingTitleException, MissingDescriptionException, MissingFishNameException, MissingPhotoException {

        // Récupération utilisateur
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        // Récupération post
        var existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundById(postId));

        // Vérification de propriété (sauf si admin)
        var userPost = existingPost.getUser();
        if (!userPost.getId().equals(existingUser.getId()) && !existingUser.isAdmin()) {
            log.warn("Tentative de modification non autorisée du post {} par l'utilisateur {}",
                    postId, existingUser.getUserName());
            throw new UnauthorizedModificationPost();
        }

        // Validation champs obligatoires
        validateData(updatedPost);

        // Mise à jour des champs
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setDescription(updatedPost.getDescription());
        existingPost.setFishName(updatedPost.getFishName());
        existingPost.setPhotoUrl(updatedPost.getPhotoUrl());
        existingPost.setWeight(updatedPost.getWeight());
        existingPost.setLength(updatedPost.getLength());
        existingPost.setLocation(updatedPost.getLocation());
        existingPost.setCatchDate(updatedPost.getCatchDate());

        var savedPost = postRepository.save(existingPost);

        log.info("Publication mise à jour par {} (Admin: {}): {}",
                existingUser.getUserName(), existingUser.isAdmin(), savedPost.getTitle());

        return savedPost;
    }

    public void deletePost(UUID userId, UUID postId)
            throws UserNotFoundById, PostNotFoundById, UnauthorizedModificationPost {

        // Récupération utilisateur
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        // Récupération post
        var existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundById(postId));

        // Vérification de propriété (sauf si admin)
        var userPost = existingPost.getUser();
        if (!userPost.getId().equals(existingUser.getId()) && !existingUser.isAdmin()) {
            log.warn("Tentative de suppression non autorisée du post {} par l'utilisateur {}",
                    postId, existingUser.getUserName());
            throw new UnauthorizedModificationPost();
        }

        postRepository.delete(existingPost);

        log.info("Publication supprimée par {} (Admin: {}): {}",
                existingUser.getUserName(), existingUser.isAdmin(), existingPost.getTitle());
    }

    // =============== MÉTHODES DE RECHERCHE ===============

    public List<PostModel> getAll() {
        var posts = postRepository.findAll();

        log.debug("Récupération de toutes les publications: {} posts trouvés", posts.size());

        return posts;
    }

    public List<PostModel> getByUserUserName(String userName) throws UserNotFoundByUserName {
        if (!userRepository.existsByUserName(userName)) {
            throw new UserNotFoundByUserName(userName);
        }

        var posts = postRepository.findByUserUserName(userName);

        log.debug("Publications de {}: {} posts trouvés", userName, posts.size());

        return posts;
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

    // =============== MÉTHODES ADMIN UNIQUEMENT ===============

    /**
     * Supprime n'importe quelle publication (ADMIN UNIQUEMENT)
     * Cette méthode bypasse les vérifications de propriété
     */
    public void deletePostByAdmin(UUID postId) throws PostNotFoundById {
        var existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundById(postId));

        postRepository.delete(existingPost);

        log.info("Publication supprimée par un administrateur: {} (Auteur: {})",
                existingPost.getTitle(), existingPost.getUser().getUserName());
    }
}