package com.FishOn.FishOn.Service;

import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.UserRepository;
import com.FishOn.FishOn.Repository.PostRepository;
import com.FishOn.FishOn.Repository.CommentRepository;
import com.FishOn.FishOn.Exception.FishOnException.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des commentaires
 * LOMBOK UTILISÉ :
 * @RequiredArgsConstructor : Injection par constructeur automatique pour les champs final
 * @Slf4j : Logger automatique disponible via log.info(), log.error(), etc.
 */
@Service
@RequiredArgsConstructor // LOMBOK : Remplace @Autowired
@Slf4j // LOMBOK : Logger automatique
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // =============== MÉTHODES CRUD STANDARD ===============

    public CommentModel createComment(CommentModel comment, UUID userId, UUID postId)
            throws UserNotFoundById, PostNotFoundById {

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundById(postId));

        // Associations
        comment.setUser(user);
        comment.setPost(post);

        var savedComment = commentRepository.save(comment);

        log.info("Nouveau commentaire créé par {} sur le post '{}': {}",
                user.getUserName(), post.getTitle(), savedComment.getContent());

        return savedComment;
    }

    public CommentModel updateComment(UUID commentId, CommentModel updatedComment, UUID userId)
            throws CommentNotFound, UnauthorizedAccess {

        var existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFound(commentId));

        // Récupération de l'utilisateur pour vérifier s'il est admin
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé")); // Ne devrait pas arriver

        // Vérification de propriété (sauf si admin)
        if (!existingComment.getUser().getId().equals(userId) && !user.isAdmin()) {
            log.warn("Tentative de modification non autorisée du commentaire {} par l'utilisateur {}",
                    commentId, user.getUserName());
            throw new UnauthorizedAccess();
        }

        // Mise à jour du contenu
        existingComment.setContent(updatedComment.getContent());

        var savedComment = commentRepository.save(existingComment);

        log.info("Commentaire mis à jour par {} (Admin: {}): {}",
                user.getUserName(), user.isAdmin(), savedComment.getContent());

        return savedComment;
    }

    public void deleteComment(UUID commentId, UUID userId) throws CommentNotFound, UnauthorizedAccess {
        var existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFound(commentId));

        // Récupération de l'utilisateur pour vérifier s'il est admin
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé")); // Ne devrait pas arriver

        // Vérification de propriété (sauf si admin)
        if (!existingComment.getUser().getId().equals(userId) && !user.isAdmin()) {
            log.warn("Tentative de suppression non autorisée du commentaire {} par l'utilisateur {}",
                    commentId, user.getUserName());
            throw new UnauthorizedAccess();
        }

        commentRepository.delete(existingComment);

        log.info("Commentaire supprimé par {} (Admin: {}): {}",
                user.getUserName(), user.isAdmin(), existingComment.getContent());
    }

    // =============== MÉTHODES DE RECHERCHE ===============

    public List<CommentModel> getByUserId(UUID userId) throws UserNotFoundById {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundById(userId);
        }

        var comments = commentRepository.findByUserId(userId);

        log.debug("Commentaires de l'utilisateur {}: {} commentaires trouvés", userId, comments.size());

        return comments;
    }

    public List<CommentModel> getByPostId(UUID postId) throws PostNotFoundById {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundById(postId);
        }

        var comments = commentRepository.findByPostId(postId);

        log.debug("Commentaires du post {}: {} commentaires trouvés", postId, comments.size());

        return comments;
    }

    /**
     * Supprime n'importe quel commentaire (ADMIN UNIQUEMENT)
     * Cette méthode bypasse les vérifications de propriété
     */
    public void deleteCommentByAdmin(UUID commentId) throws CommentNotFound {
        var existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFound(commentId));

        commentRepository.delete(existingComment);

        log.info("Commentaire supprimé par un administrateur: {} (Auteur: {})",
                existingComment.getContent(), existingComment.getUser().getUserName());
    }
}