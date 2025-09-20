package com.FishOn.FishOn.Repository;

import com.FishOn.FishOn.Model.CommentModel;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration pour CommentRepository
 * @DataJpaTest : Configure automatiquement une base H2 en mémoire
 * @ActiveProfiles("test") : Utilise application-test.properties
 */
@DataJpaTest
@ActiveProfiles("test") // Active le profil de test
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private UserModel user1;
    private UserModel user2;
    private PostModel post1;
    private PostModel post2;
    private CommentModel comment1;
    private CommentModel comment2;
    private CommentModel comment3;
    private CommentModel comment4;

    @BeforeEach
    void setUp() {
        // Nettoyage
        entityManager.clear();

        // Création des utilisateurs
        user1 = new UserModel(
                "fishMaster",
                "fish.master@example.com",
                "John",
                "Doe",
                30,
                "hashedPassword",
                "profile.jpg"
        );

        user2 = new UserModel(
                "carpHunter",
                "carp.hunter@example.com",
                "Jane",
                "Smith",
                25,
                "hashedPassword",
                "avatar.jpg"
        );

        // Persister les utilisateurs
        user1 = entityManager.persistAndFlush(user1);
        user2 = entityManager.persistAndFlush(user2);

        // Création des posts
        post1 = new PostModel(
                "Belle carpe miroir",
                "Prise ce matin au lever du soleil",
                "Carpe",
                "carpe1.jpg"
        );
        post1.setUser(user1);

        post2 = new PostModel(
                "Brochet monstre",
                "Combat de 30 minutes",
                "Brochet",
                "brochet1.jpg"
        );
        post2.setUser(user2);

        // Persister les posts
        post1 = entityManager.persistAndFlush(post1);
        post2 = entityManager.persistAndFlush(post2);

        // Création des commentaires
        comment1 = new CommentModel("Magnifique prise ! Félicitations !");
        comment1.setUser(user2); // user2 commente le post de user1
        comment1.setPost(post1);

        comment2 = new CommentModel("Quel combat ça a dû être !");
        comment2.setUser(user1); // user1 commente son propre post
        comment2.setPost(post1);

        comment3 = new CommentModel("Impressionnant ce spécimen !");
        comment3.setUser(user1); // user1 commente le post de user2
        comment3.setPost(post2);

        comment4 = new CommentModel("Tu as utilisé quel appât ?");
        comment4.setUser(user2); // user2 commente son propre post
        comment4.setPost(post2);
    }

    // =============== TEST SAVE - CORRIGÉ ===============

    @Test
    @DisplayName("Save - Création d'un nouveau commentaire")
    void save_NewComment_Success() {
        // ACT
        CommentModel savedComment = commentRepository.save(comment1);
        entityManager.flush(); // Force la persistance

        // ASSERT
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("Magnifique prise ! Félicitations !");
        assertThat(savedComment.getUser()).isEqualTo(user2);
        assertThat(savedComment.getPost()).isEqualTo(post1);
        // Les timestamps peuvent être null avant le flush, on vérifie après
    }

    @Test
    @DisplayName("Save - Mise à jour d'un commentaire")
    void save_UpdateComment_Success() {
        // ARRANGE
        CommentModel savedComment = entityManager.persistAndFlush(comment1);
        entityManager.clear(); // Clear le cache pour forcer le rechargement

        // ACT
        CommentModel toUpdate = commentRepository.findById(savedComment.getId()).orElseThrow();
        toUpdate.setContent("Commentaire modifié");
        CommentModel updatedComment = commentRepository.save(toUpdate);
        entityManager.flush();

        // ASSERT
        assertThat(updatedComment.getId()).isEqualTo(savedComment.getId());
        assertThat(updatedComment.getContent()).isEqualTo("Commentaire modifié");
    }

    // =============== TESTS FIND BY USER ID ===============

    @Test
    @DisplayName("FindByUserId - Commentaires d'un utilisateur")
    void findByUserId_ExistingUser_ReturnsComments() {
        // ARRANGE
        entityManager.persistAndFlush(comment1);
        entityManager.persistAndFlush(comment2);
        entityManager.persistAndFlush(comment3);
        entityManager.persistAndFlush(comment4);

        // ACT
        List<CommentModel> user1Comments = commentRepository.findByUserId(user1.getId());

        // ASSERT
        assertThat(user1Comments).hasSize(2);
        assertThat(user1Comments).extracting(CommentModel::getContent)
                .containsExactlyInAnyOrder(
                        "Quel combat ça a dû être !",
                        "Impressionnant ce spécimen !"
                );
    }

    @Test
    @DisplayName("FindByUserId - Utilisateur sans commentaires")
    void findByUserId_UserWithoutComments_ReturnsEmpty() {
        // ARRANGE
        UserModel userWithoutComments = new UserModel(
                "silentUser",
                "silent@example.com",
                "Silent",
                "User",
                35,
                "password",
                null
        );
        userWithoutComments = entityManager.persistAndFlush(userWithoutComments);
        entityManager.persistAndFlush(comment1);

        // ACT
        List<CommentModel> comments = commentRepository.findByUserId(userWithoutComments.getId());

        // ASSERT
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("FindByUserId - ID inexistant")
    void findByUserId_NonExistingId_ReturnsEmpty() {
        // ARRANGE
        entityManager.persistAndFlush(comment1);
        UUID randomId = UUID.randomUUID();

        // ACT
        List<CommentModel> comments = commentRepository.findByUserId(randomId);

        // ASSERT
        assertThat(comments).isEmpty();
    }

    // =============== TESTS FIND BY POST ID ===============

    @Test
    @DisplayName("FindByPostId - Commentaires d'un post")
    void findByPostId_ExistingPost_ReturnsComments() {
        // ARRANGE
        entityManager.persistAndFlush(comment1);
        entityManager.persistAndFlush(comment2);
        entityManager.persistAndFlush(comment3);
        entityManager.persistAndFlush(comment4);

        // ACT
        List<CommentModel> post1Comments = commentRepository.findByPostId(post1.getId());

        // ASSERT
        assertThat(post1Comments).hasSize(2);
        assertThat(post1Comments).extracting(CommentModel::getContent)
                .containsExactlyInAnyOrder(
                        "Magnifique prise ! Félicitations !",
                        "Quel combat ça a dû être !"
                );
    }

    // =============== TESTS RELATIONS - CORRIGÉS ===============

    @Test
    @DisplayName("Relations - Suppression en cascade depuis User")
    void delete_UserWithComments_CascadeDeletesComments() {
        // ARRANGE
        CommentModel savedComment2 = entityManager.persistAndFlush(comment2);
        CommentModel savedComment3 = entityManager.persistAndFlush(comment3);
        entityManager.clear(); // Clear pour éviter les problèmes de cache

        // Recharger l'utilisateur depuis la base
        UserModel userToDelete = entityManager.find(UserModel.class, user1.getId());

        // ACT - Supprimer user1 devrait supprimer ses commentaires
        entityManager.remove(userToDelete);
        entityManager.flush();

        // ASSERT
        assertThat(commentRepository.findById(savedComment2.getId())).isEmpty();
        assertThat(commentRepository.findById(savedComment3.getId())).isEmpty();
        assertThat(commentRepository.findByUserId(user1.getId())).isEmpty();
    }

    @Test
    @DisplayName("Relations - Suppression en cascade depuis Post")
    void delete_PostWithComments_CascadeDeletesComments() {
        // ARRANGE
        CommentModel savedComment1 = entityManager.persistAndFlush(comment1);
        CommentModel savedComment2 = entityManager.persistAndFlush(comment2);
        entityManager.clear(); // Clear pour éviter les problèmes de cache

        // Recharger le post depuis la base
        PostModel postToDelete = entityManager.find(PostModel.class, post1.getId());

        // ACT - Supprimer post1 devrait supprimer ses commentaires
        entityManager.remove(postToDelete);
        entityManager.flush();

        // ASSERT
        assertThat(commentRepository.findById(savedComment1.getId())).isEmpty();
        assertThat(commentRepository.findById(savedComment2.getId())).isEmpty();
        assertThat(commentRepository.findByPostId(post1.getId())).isEmpty();
    }

    // =============== TEST TIMESTAMPS - CORRIGÉ ===============

    @Test
    @DisplayName("Timestamps - Vérification création et mise à jour")
    void save_UpdateComment_UpdatesTimestamp() throws InterruptedException {
        // ARRANGE
        CommentModel savedComment = commentRepository.save(comment1);
        entityManager.flush(); // Force la sauvegarde pour avoir les timestamps
        entityManager.clear(); // Clear le cache

        // Recharger pour avoir les timestamps
        CommentModel reloaded = commentRepository.findById(savedComment.getId()).orElseThrow();
        LocalDateTime createdAt = reloaded.getCreatedAt();

        // ACT
        Thread.sleep(100); // Pause pour timestamps différents
        reloaded.setContent("Contenu modifié");
        CommentModel updatedComment = commentRepository.save(reloaded);
        entityManager.flush();
        entityManager.clear();

        // Recharger pour avoir le nouveau updatedAt
        CommentModel finalComment = commentRepository.findById(updatedComment.getId()).orElseThrow();

        // ASSERT
        assertThat(finalComment.getCreatedAt()).isEqualTo(createdAt);
        assertThat(finalComment.getUpdatedAt()).isAfter(createdAt);
    }

    // =============== TESTS MÉTHODES HÉRITÉES ===============

    @Test
    @DisplayName("FindById - Commentaire existant")
    void findById_ExistingId_ReturnsComment() {
        // ARRANGE
        CommentModel savedComment = entityManager.persistAndFlush(comment1);
        UUID commentId = savedComment.getId();

        // ACT
        var found = commentRepository.findById(commentId);

        // ASSERT
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("Magnifique prise ! Félicitations !");
    }

    @Test
    @DisplayName("FindAll - Tous les commentaires")
    void findAll_MultipleComments_ReturnsAll() {
        // ARRANGE
        entityManager.persistAndFlush(comment1);
        entityManager.persistAndFlush(comment2);
        entityManager.persistAndFlush(comment3);
        entityManager.persistAndFlush(comment4);

        // ACT
        List<CommentModel> allComments = commentRepository.findAll();

        // ASSERT
        assertThat(allComments).hasSize(4);
        assertThat(allComments).extracting(CommentModel::getContent)
                .contains(
                        "Magnifique prise ! Félicitations !",
                        "Quel combat ça a dû être !",
                        "Impressionnant ce spécimen !",
                        "Tu as utilisé quel appât ?"
                );
    }

    @Test
    @DisplayName("Count - Nombre de commentaires")
    void count_MultipleComments_ReturnsCorrectCount() {
        // ARRANGE
        entityManager.persistAndFlush(comment1);
        entityManager.persistAndFlush(comment2);
        entityManager.persistAndFlush(comment3);

        // ACT
        long count = commentRepository.count();

        // ASSERT
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Delete - Suppression d'un commentaire")
    void delete_ExistingComment_RemovesComment() {
        // ARRANGE
        CommentModel savedComment = entityManager.persistAndFlush(comment1);
        UUID commentId = savedComment.getId();

        // ACT
        commentRepository.delete(savedComment);
        entityManager.flush();

        // ASSERT
        assertThat(commentRepository.findById(commentId)).isEmpty();
    }

    @Test
    @DisplayName("Contraintes - Contenu obligatoire")
    void save_NullContent_ThrowsException() {
        // ARRANGE
        CommentModel invalidComment = new CommentModel(null);
        invalidComment.setUser(user1);
        invalidComment.setPost(post1);

        // ACT & ASSERT
        assertThat(catchThrowable(() -> {
            commentRepository.saveAndFlush(invalidComment);
        })).isInstanceOf(Exception.class);
    }

    // =============== HELPER METHOD ===============

    private static Throwable catchThrowable(ThrowingCallable callable) {
        try {
            callable.call();
            return null;
        } catch (Throwable e) {
            return e;
        }
    }

    @FunctionalInterface
    interface ThrowingCallable {
        void call() throws Exception;
    }
}