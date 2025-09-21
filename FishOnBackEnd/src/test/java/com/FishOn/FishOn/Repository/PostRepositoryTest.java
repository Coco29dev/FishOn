package com.FishOn.FishOn.Repository;

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
 * Tests d'intégration pour PostRepository
 * Teste les requêtes personnalisées et les relations avec UserModel
 */
@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    private UserModel user1;
    private UserModel user2;
    private PostModel post1;
    private PostModel post2;
    private PostModel post3;

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

        // Persister les utilisateurs d'abord
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
        post1.setWeight(12.5);
        post1.setLength(65.0);
        post1.setLocation("Lac de Sologne");
        post1.setCatchDate(LocalDateTime.now().minusDays(1));

        post2 = new PostModel(
                "Brochet monstre",
                "Combat de 30 minutes",
                "Brochet",
                "brochet1.jpg"
        );
        post2.setUser(user1);
        post2.setWeight(8.2);
        post2.setLength(95.0);
        post2.setLocation("Rivière Seine");
        post2.setCatchDate(LocalDateTime.now().minusDays(2));

        post3 = new PostModel(
                "Carpe commune",
                "Première de la saison",
                "Carpe",
                "carpe2.jpg"
        );
        post3.setUser(user2);
        post3.setWeight(15.0);
        post3.setLength(70.0);
        post3.setLocation("Lac de Sologne");
        post3.setCatchDate(LocalDateTime.now().minusDays(3));
    }

    // =============== TESTS SAVE ===============

    @Test
    @DisplayName("Save - Création d'un nouveau post avec utilisateur")
    void save_NewPost_Success() {
        // ACT
        PostModel savedPost = postRepository.save(post1);

        // ASSERT
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("Belle carpe miroir");
        assertThat(savedPost.getDescription()).isEqualTo("Prise ce matin au lever du soleil");
        assertThat(savedPost.getFishName()).isEqualTo("Carpe");
        assertThat(savedPost.getUser()).isEqualTo(user1);
        assertThat(savedPost.getCreatedAt()).isNotNull();
        assertThat(savedPost.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Save - Post avec données optionnelles")
    void save_PostWithOptionalData_Success() {
        // ACT
        PostModel savedPost = postRepository.save(post1);

        // ASSERT
        assertThat(savedPost.getWeight()).isEqualTo(12.5);
        assertThat(savedPost.getLength()).isEqualTo(65.0);
        assertThat(savedPost.getLocation()).isEqualTo("Lac de Sologne");
        assertThat(savedPost.getCatchDate()).isNotNull();
    }

    // =============== TESTS FIND BY USER USERNAME ===============

    @Test
    @DisplayName("FindByUserUserName - Posts d'un utilisateur existant")
    void findByUserUserName_ExistingUser_ReturnsPosts() {
        // ARRANGE
        entityManager.persistAndFlush(post1);
        entityManager.persistAndFlush(post2);
        entityManager.persistAndFlush(post3);

        // ACT
        List<PostModel> userPosts = postRepository.findByUserUserName("fishMaster");

        // ASSERT
        assertThat(userPosts).hasSize(2);
        assertThat(userPosts).extracting(PostModel::getTitle)
                .containsExactlyInAnyOrder("Belle carpe miroir", "Brochet monstre");
    }

    @Test
    @DisplayName("FindByUserUserName - Utilisateur sans posts")
    void findByUserUserName_UserWithoutPosts_ReturnsEmpty() {
        // ARRANGE
        UserModel userWithoutPosts = new UserModel(
                "noFish",
                "no.fish@example.com",
                "No",
                "Fish",
                20,
                "password",
                null
        );
        entityManager.persistAndFlush(userWithoutPosts);
        entityManager.persistAndFlush(post1);

        // ACT
        List<PostModel> posts = postRepository.findByUserUserName("noFish");

        // ASSERT
        assertThat(posts).isEmpty();
    }

    @Test
    @DisplayName("FindByUserUserName - Utilisateur inexistant")
    void findByUserUserName_NonExistingUser_ReturnsEmpty() {
        // ARRANGE
        entityManager.persistAndFlush(post1);

        // ACT
        List<PostModel> posts = postRepository.findByUserUserName("unknownUser");

        // ASSERT
        assertThat(posts).isEmpty();
    }

    // =============== TESTS FIND BY USER ID ===============

    @Test
    @DisplayName("FindByUserId - Posts par ID utilisateur")
    void findByUserId_ExistingUserId_ReturnsPosts() {
        // ARRANGE
        entityManager.persistAndFlush(post1);
        entityManager.persistAndFlush(post2);
        entityManager.persistAndFlush(post3);
        UUID userId = user1.getId();

        // ACT
        List<PostModel> userPosts = postRepository.findByUserId(userId);

        // ASSERT
        assertThat(userPosts).hasSize(2);
        assertThat(userPosts).allMatch(post -> post.getUser().getId().equals(userId));
    }

    @Test
    @DisplayName("FindByUserId - ID inexistant")
    void findByUserId_NonExistingId_ReturnsEmpty() {
        // ARRANGE
        entityManager.persistAndFlush(post1);
        UUID randomId = UUID.randomUUID();

        // ACT
        List<PostModel> posts = postRepository.findByUserId(randomId);

        // ASSERT
        assertThat(posts).isEmpty();
    }

    // =============== TESTS FIND BY FISH NAME ===============

    @Test
    @DisplayName("FindByFishName - Posts par nom de poisson")
    void findByFishName_ExistingFish_ReturnsPosts() {
        // ARRANGE
        entityManager.persistAndFlush(post1);
        entityManager.persistAndFlush(post2);
        entityManager.persistAndFlush(post3);

        // ACT
        List<PostModel> carpePosts = postRepository.findByFishName("Carpe");

        // ASSERT
        assertThat(carpePosts).hasSize(2);
        assertThat(carpePosts).extracting(PostModel::getFishName)
                .containsOnly("Carpe");
    }

    @Test
    @DisplayName("FindByFishName - Nom de poisson inexistant")
    void findByFishName_NonExistingFish_ReturnsEmpty() {
        // ARRANGE
        entityManager.persistAndFlush(post1);
        entityManager.persistAndFlush(post2);

        // ACT
        List<PostModel> posts = postRepository.findByFishName("Truite");

        // ASSERT
        assertThat(posts).isEmpty();
    }

    @Test
    @DisplayName("FindByFishName - Sensibilité à la casse")
    void findByFishName_CaseSensitive_ReturnsEmpty() {
        // ARRANGE
        entityManager.persistAndFlush(post1);

        // ACT
        List<PostModel> posts = postRepository.findByFishName("CARPE");

        // ASSERT
        assertThat(posts).isEmpty(); // Case sensitive par défaut
    }

    // =============== TESTS FIND BY LOCATION ===============

    @Test
    @DisplayName("FindByLocation - Posts par lieu")
    void findByLocation_ExistingLocation_ReturnsPosts() {
        // ARRANGE
        entityManager.persistAndFlush(post1);
        entityManager.persistAndFlush(post2);
        entityManager.persistAndFlush(post3);

        // ACT
        List<PostModel> lacPosts = postRepository.findByLocation("Lac de Sologne");

        // ASSERT
        assertThat(lacPosts).hasSize(2);
        assertThat(lacPosts).extracting(PostModel::getLocation)
                .containsOnly("Lac de Sologne");
    }

    @Test
    @DisplayName("FindByLocation - Lieu inexistant")
    void findByLocation_NonExistingLocation_ReturnsEmpty() {
        // ARRANGE
        entityManager.persistAndFlush(post1);

        // ACT
        List<PostModel> posts = postRepository.findByLocation("Océan Atlantique");

        // ASSERT
        assertThat(posts).isEmpty();
    }

    // =============== TESTS EXISTS BY ===============

    @Test
    @DisplayName("ExistsByFishName - Nom de poisson existant")
    void existsByFishName_ExistingFish_ReturnsTrue() {
        // ARRANGE
        entityManager.persistAndFlush(post1);

        // ACT
        boolean exists = postRepository.existsByFishName("Carpe");

        // ASSERT
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("ExistsByFishName - Nom de poisson inexistant")
    void existsByFishName_NonExistingFish_ReturnsFalse() {
        // ARRANGE
        entityManager.persistAndFlush(post1);

        // ACT
        boolean exists = postRepository.existsByFishName("Saumon");

        // ASSERT
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("ExistsByLocation - Lieu existant")
    void existsByLocation_ExistingLocation_ReturnsTrue() {
        // ARRANGE
        entityManager.persistAndFlush(post1);

        // ACT
        boolean exists = postRepository.existsByLocation("Lac de Sologne");

        // ASSERT
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("ExistsByLocation - Lieu inexistant")
    void existsByLocation_NonExistingLocation_ReturnsFalse() {
        // ARRANGE
        entityManager.persistAndFlush(post1);

        // ACT
        boolean exists = postRepository.existsByLocation("Mer Méditerranée");

        // ASSERT
        assertThat(exists).isFalse();
    }

    // =============== TESTS MÉTHODES HÉRITÉES ===============

    @Test
    @DisplayName("FindAll - Récupération de tous les posts")
    void findAll_MultiplePosts_ReturnsAllPosts() {
        // ARRANGE
        entityManager.persistAndFlush(post1);
        entityManager.persistAndFlush(post2);
        entityManager.persistAndFlush(post3);

        // ACT
        List<PostModel> allPosts = postRepository.findAll();

        // ASSERT
        assertThat(allPosts).hasSize(3);
        assertThat(allPosts).extracting(PostModel::getTitle)
                .containsExactlyInAnyOrder(
                        "Belle carpe miroir",
                        "Brochet monstre",
                        "Carpe commune"
                );
    }

    @Test
    @DisplayName("Count - Nombre total de posts")
    void count_MultiplePosts_ReturnsCorrectCount() {
        // ARRANGE
        entityManager.persistAndFlush(post1);
        entityManager.persistAndFlush(post2);
        entityManager.persistAndFlush(post3);

        // ACT
        long count = postRepository.count();

        // ASSERT
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Delete - Suppression d'un post")
    void delete_ExistingPost_RemovesPost() {
        // ARRANGE
        PostModel savedPost = entityManager.persistAndFlush(post1);
        UUID postId = savedPost.getId();

        // ACT
        postRepository.delete(savedPost);
        entityManager.flush();

        // ASSERT
        assertThat(postRepository.findById(postId)).isEmpty();
        assertThat(postRepository.count()).isZero();
    }

    // =============== TESTS RELATIONS ===============

    @Test
    @DisplayName("Relations - Post avec User")
    void save_PostWithUser_MaintainsRelation() {
        // ARRANGE & ACT
        PostModel savedPost = postRepository.save(post1);
        entityManager.flush();
        entityManager.clear(); // Clear cache pour forcer le rechargement

        // ASSERT
        PostModel foundPost = postRepository.findById(savedPost.getId()).orElseThrow();
        assertThat(foundPost.getUser()).isNotNull();
        assertThat(foundPost.getUser().getUserName()).isEqualTo("fishMaster");
    }

    @Test
    @DisplayName("Relations - Suppression en cascade")
    void delete_UserWithPosts_CascadeDeletesPosts() {
        // ARRANGE
        PostModel savedPost1 = entityManager.persistAndFlush(post1);
        PostModel savedPost2 = entityManager.persistAndFlush(post2);

        // ACT - Supprimer l'utilisateur devrait supprimer ses posts (cascade)
        entityManager.remove(user1);
        entityManager.flush();

        // ASSERT
        assertThat(postRepository.findById(savedPost1.getId())).isEmpty();
        assertThat(postRepository.findById(savedPost2.getId())).isEmpty();
        assertThat(postRepository.findByUserId(user1.getId())).isEmpty();
    }

    // =============== TESTS REQUÊTES COMPLEXES ===============

    @Test
    @DisplayName("Requête complexe - Posts multiples critères")
    void findPosts_MultipleUsers_MultipleFish_ReturnsCorrectPosts() {
        // ARRANGE
        entityManager.persistAndFlush(post1); // user1, Carpe
        entityManager.persistAndFlush(post2); // user1, Brochet
        entityManager.persistAndFlush(post3); // user2, Carpe

        // ACT
        List<PostModel> user1Posts = postRepository.findByUserId(user1.getId());
        List<PostModel> carpePosts = postRepository.findByFishName("Carpe");

        // ASSERT
        // Posts de user1
        assertThat(user1Posts).hasSize(2);
        assertThat(user1Posts).extracting(PostModel::getFishName)
                .containsExactlyInAnyOrder("Carpe", "Brochet");

        // Posts de type Carpe
        assertThat(carpePosts).hasSize(2);
        assertThat(carpePosts).extracting(p -> p.getUser().getUserName())
                .containsExactlyInAnyOrder("fishMaster", "carpHunter");
    }

    @Test
    @DisplayName("Timestamps - Vérification création et mise à jour")
    void save_UpdatePost_UpdatesTimestamp() throws InterruptedException {
        // ARRANGE
        PostModel savedPost = postRepository.save(post1);
        LocalDateTime createdAt = savedPost.getCreatedAt();

        // ACT
        Thread.sleep(100); // Pause pour avoir des timestamps différents
        savedPost.setTitle("Titre modifié");
        PostModel updatedPost = postRepository.save(savedPost);

        // ASSERT
        assertThat(updatedPost.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updatedPost.getUpdatedAt()).isAfter(createdAt);
    }
}