package com.FishOn.FishOn.Controller;

import com.FishOn.FishOn.Config.CustomUserDetails;
import com.FishOn.FishOn.Config.SecurityConfig;
import com.FishOn.FishOn.DTO.Post.PostCreateDTO;
import com.FishOn.FishOn.DTO.Post.PostUpdateDTO;
import com.FishOn.FishOn.Model.PostModel;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.PostService;
import com.FishOn.FishOn.Exception.FishOnException.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
// Teste uniquement le contrôleur PostController. Spring Boot configure un contexte léger,
// sans lancer le serveur complet ni la base de données.
@Import(SecurityConfig.class)
// Importe la configuration de sécurité afin que les tests respectent l'authentification et l'autorisation.
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    // MockMvc permet de simuler des requêtes HTTP (GET, POST, PUT, DELETE) directement sur le contrôleur
    // sans démarrer un serveur complet.

    @MockBean
    private PostService postService;
    // Mock du service. Permet d'isoler le contrôleur, les tests ne dépendent pas de la logique réelle du service.

    private ObjectMapper objectMapper;
    // Convertit les objets Java en JSON et inversement. Indispensable pour envoyer/recevoir des DTO en JSON.

    private UserModel mockUser;
    // Utilisateur simulé pour les tests, représentera le propriétaire d’un post.

    private CustomUserDetails mockUserDetails;
    // Spring Security utilise UserDetails pour l’authentification. On l’associe à notre utilisateur simulé.

    private PostModel mockPost;
    // Post simulé pour tester la création, la modification, la suppression et l’affichage.

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        // Permet à Jackson de gérer les types Java 8 comme LocalDateTime.

        // === Création d'un utilisateur simulé ===
        mockUser = new UserModel(
                "fishMaster",
                "fish.master@example.com",
                "John",
                "Doe",
                30,
                "hashedPassword",
                null
        );
        mockUser.setId(UUID.randomUUID());
        // Chaque utilisateur simulé a un ID unique.

        mockUserDetails = new CustomUserDetails(mockUser);
        // On transforme notre UserModel en CustomUserDetails pour Spring Security.

        // === Création d’un post simulé ===
        mockPost = new PostModel(
                "Titre Test",
                "Description Test",
                "Truite",
                "photoUrlTest"
        );
        mockPost.setId(UUID.randomUUID());
        mockPost.setUser(mockUser); // Association du post avec l’utilisateur.
        mockPost.setCreatedAt(LocalDateTime.now());
        mockPost.setUpdatedAt(LocalDateTime.now());
    }

    // ================= GET /feed =================
    @Test
    public void testGetFeed_authenticated() throws Exception {
        // On simule le service pour qu'il retourne notre post mocké
        when(postService.getAll()).thenReturn(List.of(mockPost));

        // On effectue la requête GET sur /api/posts/feed en simulant un utilisateur authentifié
        mockMvc.perform(get("/api/posts/feed")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk()) // Vérifie que le code HTTP est 200
                .andExpect(jsonPath("$[0].title").value("Titre Test"))
                .andExpect(jsonPath("$[0].description").value("Description Test"))
                .andExpect(jsonPath("$[0].fishName").value("Truite"))
                .andExpect(jsonPath("$[0].userName").value("fishMaster"));
    }

    @Test
    public void testGetFeed_unauthenticated() throws Exception {
        // Si l’utilisateur n’est pas connecté, l’accès doit être refusé (401 Unauthorized)
        mockMvc.perform(get("/api/posts/feed"))
                .andExpect(status().isUnauthorized());
    }

    // ================= POST / =================
    @Test
    public void testCreatePost_authenticated() throws Exception, MissingTitleException, MissingDescriptionException,
            MissingFishNameException, MissingPhotoException, UserNotFoundById {

        // === Création d’un DTO simulant la requête JSON pour créer un post ===
        PostCreateDTO createDTO = new PostCreateDTO(
                "Titre Test",
                "Description Test",
                "Truite",
                "photoUrlTest",
                1.5, // poids du poisson
                50.0, // longueur du poisson
                "Lac", // lieu de pêche
                LocalDateTime.now() // date de la prise
        );

        // === Création du post attendu après la création ===
        PostModel createdPost = new PostModel(
                createDTO.getTitle(),
                createDTO.getDescription(),
                createDTO.getFishName(),
                createDTO.getPhotoUrl()
        );
        createdPost.setId(UUID.randomUUID());
        createdPost.setUser(mockUser);
        createdPost.setCreatedAt(LocalDateTime.now());
        createdPost.setUpdatedAt(LocalDateTime.now());

        // Mock du service pour qu’il retourne notre post créé
        when(postService.createPost(eq(mockUser.getId()), any(PostModel.class))).thenReturn(createdPost);

        // Simulation de la requête POST avec authentification et CSRF
        mockMvc.perform(post("/api/posts")
                        .with(user(mockUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))) // Transformation du DTO en JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Titre Test"))
                .andExpect(jsonPath("$.description").value("Description Test"))
                .andExpect(jsonPath("$.fishName").value("Truite"))
                .andExpect(jsonPath("$.userName").value("fishMaster"));
    }

    @Test
    public void testCreatePost_unauthenticated() throws Exception {
        // Tentative de création sans authentification
        PostCreateDTO createDTO = new PostCreateDTO("Titre Test", "Desc", "Poisson", "photoUrl", null, null, null, null);

        mockMvc.perform(post("/api/posts")
                        .with(csrf()) // CSRF nécessaire même si pas authentifié
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isUnauthorized()); // Accès refusé
    }

    // ================= PUT /{postId} =================
    @Test
    public void testUpdatePost_authenticated() throws Exception, MissingTitleException, MissingDescriptionException,
            MissingFishNameException, MissingPhotoException, UserNotFoundById, PostNotFoundById, UnauthorizedModificationPost {

        PostUpdateDTO updateDTO = new PostUpdateDTO("Titre MAJ", "Desc MAJ", "Saumon", "photoMAJ", null, null, null, null);

        // Post attendu après mise à jour
        PostModel updatedPost = new PostModel(updateDTO.getTitle(), updateDTO.getDescription(),
                updateDTO.getFishName(), updateDTO.getPhotoUrl());
        updatedPost.setId(mockPost.getId());
        updatedPost.setUser(mockUser);
        updatedPost.setCreatedAt(mockPost.getCreatedAt());
        updatedPost.setUpdatedAt(LocalDateTime.now());

        // Mock du service pour la mise à jour
        when(postService.updatePost(eq(mockUser.getId()), eq(mockPost.getId()), any(PostModel.class)))
                .thenReturn(updatedPost);

        // Requête PUT simulée
        mockMvc.perform(put("/api/posts/" + mockPost.getId())
                        .with(user(mockUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Titre MAJ"))
                .andExpect(jsonPath("$.description").value("Desc MAJ"))
                .andExpect(jsonPath("$.fishName").value("Saumon"));
    }

    @Test
    public void testUpdatePost_unauthenticated() throws Exception {
        PostUpdateDTO updateDTO = new PostUpdateDTO("Titre MAJ", "Desc MAJ", "Saumon", "photoMAJ", null, null, null, null);

        mockMvc.perform(put("/api/posts/" + mockPost.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isUnauthorized()); // Doit être refusé
    }

    // ================= DELETE /{postId} =================
    @Test
    public void testDeletePost_authenticated() throws Exception, PostNotFoundById, UserNotFoundById, UnauthorizedModificationPost {
        // Simulation de suppression
        mockMvc.perform(delete("/api/posts/" + mockPost.getId())
                        .with(user(mockUserDetails))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Publication supprimé")); // Vérification du message de succès
    }

    @Test
    public void testDeletePost_unauthenticated() throws Exception {
        // Tentative de suppression sans authentification
        mockMvc.perform(delete("/api/posts/" + mockPost.getId())
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); // Doit échouer
    }
}
