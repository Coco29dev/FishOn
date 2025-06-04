package com.example.API.Peche.config;
// Annotations création d'objet Spring
import org.springframework.context.annotation.Bean;
// Annotations marquage classe comme configuration
import org.springframework.context.annotation.Configuration;
// Annotation activation Spring Security
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// Classe pour encoder password
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// Classe configuration règle de sécurité HTTP
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration // Spring marque cette classe comme configuration
@EnableWebSecurity // Active Spring Security
public class SecurityConfig {

    @Bean // ← Crée objets gérés par Spring
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encodage sécurisé
    }

    @Bean // Crée objets gérés par Spring
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configuration autorisations requêtes HTTP
        http
                .authorizeHttpRequests(auth -> auth
                        // Routes d'authentification publiques
                        .requestMatchers("/api/auth/**").permitAll()
                        // Voir toutes les prises (public)
                        .requestMatchers("/api/catches").permitAll()
                        // Tout le reste protégé
                        .anyRequest().authenticated())
                // Authentification HTTP Basic
                .httpBasic(Customizer.withDefaults())
                // Désactiver CSRF pour API REST
                .csrf(csrf -> csrf.disable());

        // Construction et retourne configuration sécurité
        return http.build();
    }
    
    /*/
    Flux d'exécution:
        1. Requête arrive → Spring Security intercepte
        2. Vérifie les règles authorizeHttpRequests :
        - /api/auth/** → Passe sans authentification
        - /api/catches → Passe sans authentification  
        - Autre → Exige authentification HTTP Basic
        3. Si auth requise → Vérifie header Authorization
        4. Si valide → Continue vers Controller
        5. Si invalide → Retourne 401 Unauthorized
     */
}
