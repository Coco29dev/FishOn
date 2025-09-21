# Tests d'Autorisation IDOR
__Tests de sécurité__ contre les vulnérabilités __IDOR__ (Insecure Direct Object Reference).

Protègent l'application contre les tentatives d'accès non autorisé aux ressources d'autrui par manipulation d'__identifiants__ dans les `URLs` ou les requêtes.

# Architecture Tests IDOR
```bash
Security/Authorization/
├── PostAuthorizationTest.java      # 🔒 Protection publications  
├── CommentAuthorizationTest.java   # 🔒 Protection commentaires
└── UserAuthorizationTest.java      # 🔒 Protection profils
```

# Annotations Spécialisées
```java
@WebMvcTest(controllers = PostController.class)
@Import(SecurityConfig.class)
class PostAuthorizationTest {
}
```

- `@WebMvcTest`: Test __slice__ focalisé sur la couche `Controller`.
- `@Import(SecurityConfig.class)`: Importe la configuration __Spring Security__ réelle.

# Pattern AAA Sécuritaire
__Arrange-Act-Assert__ adapté pour simuler des __attaques IDOR__ et vérifier les __protections__.

```java
@Test
@DisplayName("🔒 IDOR Protection - Tentative malveillante")
void updatePost_MaliciousUser_ShouldBeBlocked() throws Exception {
    // ARRANGE - Configuration attaque IDOR
    when(postService.updatePost(eq(maliciousUserId), eq(targetPostId), any()))
        .thenThrow(new UnauthorizedModificationPost());
    
    // ACT - Simulation attaque
    mockMvc.perform(put("/api/posts/{postId}", targetPostId)
        .with(user(maliciousUserDetails))
        .with(csrf()))
    
    // ASSERT - Vérification blocage
        .andExpect(status().isForbidden());
}
```

# Personas de Test
__Simulation d'utilisateurs__ avec des __intentions différentes__ :

```java
// 👤 Utilisateur légitime (propriétaire ressource)
CustomUserDetails legitimateUserDetails = new CustomUserDetails(resourceOwner);

// 🎭 Utilisateur malveillant (tente IDOR)  
CustomUserDetails maliciousUserDetails = new CustomUserDetails(attacker);

// 🎯 Utilisateur cible (victime visée)
UserModel targetUser = createVictimUser();
```

# Outils de Simulation
__MockMvc Security__: Authentification simulée

```java
.with(user(legitimateUserDetails))    // ✅ Utilisateur autorisé
.with(user(maliciousUserDetails))     // 🎭 Attaquant 
.with(csrf())                         // 🔐 Token CSRF obligatoire
```

__Configuration des Mocks__: Comportements attendus

```java
// Succès pour propriétaire légitime
when(service.updateResource(legitimateUserId, resourceId))
    .thenReturn(updatedResource);

// Échec pour utilisateur malveillant
when(service.updateResource(maliciousUserId, resourceId))
    .thenThrow(new UnauthorizedModificationPost());
```

# Scénarios d'Attaque Testés
__IDOR Basique__: Modification ressource d'autrui
```java
PUT /api/posts/12345 avec userId != owner
→ Attendu: 403 Forbidden ✅
```

__Énumération__: Balayage de ressources
```java
for (UUID id : suspectedIds) {
    // Toutes tentatives bloquées
    .andExpect(status().isForbidden());
}
```

__Substitution d'ID__: Manipulation d'URL
```java
PUT /api/comments/{victimCommentId}
→ Attendu: 403 Forbidden ✅
```

__Cross-Domain__: Propriétaire post ≠ Propriétaire commentaire
```java
// Propriétaire du post ne peut PAS modifier commentaires d'autrui
.andExpect(status().isForbidden());
```

# Codes de Statut Sécuritaires
__Réponses HTTP__ selon les scénarios :

```java
.andExpect(status().isOk())           // 200 - Accès autorisé ✅
.andExpect(status().isUnauthorized()) // 401 - Non authentifié 🔐  
.andExpect(status().isForbidden())    // 403 - Accès refusé 🚫
.andExpect(status().isNotFound())     // 404 - Ressource inexistante 🔍
.andExpect(status().isConflict())     // 409 - Conflit unicité 🛡️
.andExpect(status().isBadRequest())   // 400 - Données invalides ⚠️
```

# Vérifications de Sécurité
__Contrôle des appels services__ :

```java
// Vérification appel avec bon utilisateur
verify(service).updateResource(eq(legitimateUserId), eq(resourceId));

// Vérification AUCUN appel avec utilisateur malveillant  
verify(service, never()).updateResource(eq(targetUserId), any());
```

__Test d'isolation__ :

```java
// Chaque utilisateur accède uniquement à SES ressources
verify(service, times(2)).updateResource(eq(currentUserId), any());
verify(service, never()).updateResource(eq(otherUserId), any());
```

# Couverture par Domaine
__PostAuthorizationTest__ : Publications
- Modification post d'autrui → `403`
- Suppression post d'autrui → `403`
- Lecture publique vs modification privée → Distinction
- Énumération multiple posts → Blocage systématique

__CommentAuthorizationTest__ : Commentaires
- Modification commentaire d'autrui → `403`
- Propriétaire post ≠ Propriétaire commentaire → `403`
- Lecture commentaires publics → `200`
- Validation contenu malveillant → `400`

__UserAuthorizationTest__ : Profils
- Endpoint `/me` auto-référentiel → Sécurité par design
- Isolation complète des profils → ID authentification
- Recherche publique lecture seule → `200`
- Tentatives usurpation identité → ID préservé
