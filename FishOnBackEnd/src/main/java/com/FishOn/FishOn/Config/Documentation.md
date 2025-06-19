# Vue d'ensemble
__Couche__ `Config` constitue le __système de configuration de `Spring Security`__.

Définit les __règles d'authentification__, les __politiques de sécurité__ et __l'intégration__ entre le modèle utilisateur métier et le __module__ `Spring Security`.

Assure la __sécurisation__ de l'`API REST` et la __gestion des sessions__ utilisateurs.

## Configuration Spring Security
- __Définition__ des règles d'accès aux __endpoints__.
- __Configuration__ de l'authentification par __session `HTTP`__.
- __Intégration__ avec le système utilisateur métier.
- __Gestion__ des politiques de session.

## Intégration métier/sécurité
- __Pont__ entre `UserModel` et `UserDetails Spring Security`.
- __Service d'authentification__ personnalisé.
- __Chargement__ des utilisateurs depuis la __base de données__.
- __Adaptation__ du modèle métier aux interfaces `Spring`.

## Sécurit Web
- __Configuration__ `CORS` pour `frontend React`.
- __Gestion__ des cookies de session.
- __Désactivation__ `CSRF` pour `API REST`.
- __Encodage__ sécurisé des mots de passe.

# Architecture Configuration
```bash
Config/
├── SecurityConfig.java           # Configuration principale Spring Security
├── CustomUserDetailsService.java # Service de chargement utilisateur
└── CustomUserDetails.java        # Adaptateur UserModel → UserDetails
```

# Conceptions et Annotations Spring
`@Configuration` / `@EnableWebSecurity`
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // @Configuration : Classe de configuration Spring
    // @EnableWebSecurity : Active le système de sécurité web Spring
    // Charge les filtres de sécurité automatiquement
}
```

`@Bean`
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
// Définit un composant géré par Spring
// Disponible pour injection dans toute l'application
// Cycle de vie géré par le conteneur Spring
```

`@Service`
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    // Implémentation personnalisée de UserDetailsService
    // Service Spring automatiquement détecté par Spring Security
    // Intégration transparente avec AuthenticationManager
}
```
