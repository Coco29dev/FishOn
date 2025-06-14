package com.FishOn.FishOn.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration // Dit à Spring cette classe contient configuration à charger au démarage
@EnableWebSecurity // Activation Spring Security et ses filtres de sécurité
public class SecurityConfig {

    @Bean // Spring gère cet objet et le rend disponible partout
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Création encodeur BCrypt(algorithme de haschage sécurisé)
    }

    @Bean // Spring gère cet objet et le rend disponible partout
    // Méthode configuration sécurité application
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // Configuration des autorisations
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Routes d'auth publiques
                .anyRequest().authenticated()) // Tout le reste protégé
                
            // Configuration des sessions
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Création session lors de connexion
                .maximumSessions(1) // 1 session par utilisateur
                .maxSessionsPreventsLogin(false)) // Si reconnexion, ancienne session fermé et crée une nouvelle
                
            // Désactiver CSRF pour API REST
            .csrf(csrf -> csrf.disable())
            
            // Configuration CORS pour React
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            .build(); // Construit et retourne objet de configuration
    }

    /**
     * Configuration CORS pour permettre à React d'appeler l'API
     */
    @Bean // Spring gère cet objet et le rend disponible partout
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000")); // Autoristaion requête 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Autorisation Méthode
        configuration.setAllowedHeaders(Arrays.asList("*")); // Autorisation headers
        configuration.setAllowCredentials(true); // Permet envoie cookies session entre Spring et React
        
        // Création gestionnaire configuration CORS basé sur URL
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Application config CORS à toutes les URLs
        source.registerCorsConfiguration("/api/**", configuration);
        // Retourne configuration à Spring
        return source;
    }
}
