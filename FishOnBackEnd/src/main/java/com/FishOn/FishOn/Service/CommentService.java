package com.FishOn.FishOn.Service;

import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.UserRepository;
import com.FishOn.FishOn.Repository.PostRepository;
import com.FishOn.FishOn.Repository.CommentRepository;
import com.FishOn.FishOn.Exception.FishOnException.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // Méthode CRUD
    public CommentModel createComment(CommentModel comment, UUID userId, UUID postId) throws  UserNotFoundById, PostNotFoundById {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        PostModel post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundById(postId));

        comment.setUser(user);
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    public CommentModel updateComment(UUID commentId, CommentModel updatedComment, UUID userId) throws CommentNotFound, UnauthorizedAccess {
        CommentModel existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFound(commentId));

        if (!existingComment.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccess();
        }
        existingComment.setContent(updatedComment.getContent());
        return commentRepository.save(existingComment);
    }

    public void deleteComment(UUID commentId, UUID userId) throws CommentNotFound, UnauthorizedAccess {
        CommentModel existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFound(commentId));

        if (!existingComment.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccess();
        }
        commentRepository.delete(existingComment);
        System.out.println("Le commentaire " + commentId + " bien été supprimé");
    }

    // Méthode Repository
    public List<CommentModel> getByUserId(UUID userId) throws UserNotFoundById {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundById(userId);
        }
        return commentRepository.findByUserId(userId);
    }

    public List<CommentModel> getByPostId(UUID postId) throws PostNotFoundById {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundById(postId);
        }
        return commentRepository.findByPostId(postId);
    }
}