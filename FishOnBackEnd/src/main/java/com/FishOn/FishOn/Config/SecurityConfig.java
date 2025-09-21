package com.FishOn.FishOn.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Configuration des autorisations
                .authorizeHttpRequests(auth -> auth
                        // ========== RESSOURCES STATIQUES PUBLIQUES ==========
                        .requestMatchers("/", "/index.html").permitAll()
                        .requestMatchers("/HTML/**").permitAll()    // Toutes les pages HTML
                        .requestMatchers("/CSS/**").permitAll()     // Tous les fichiers CSS
                        .requestMatchers("/JS/**").permitAll()      // Tous les fichiers JavaScript
                        .requestMatchers("/IMG/**").permitAll()     // Toutes les images
                        .requestMatchers("/favicon.ico").permitAll() // Favicon

                        // ========== RESSOURCES BACKEND PUBLIQUES ==========
                        .requestMatchers("/profilePicture/**").permitAll() // Images de profil
                        .requestMatchers("/fishPicture/**").permitAll()    // Images de poissons

                        // ========== ROUTES PUBLIQUES ==========
                        .requestMatchers("/api/auth/**").permitAll()          // Routes d'authentification
                        .requestMatchers("/api/users/search/**").permitAll()  // Recherche publique

                        // ========== ROUTES WEB CONTROLLER ==========
                        .requestMatchers("/login", "/register", "/feed", "/profile", "/journal").permitAll()

                        // ========== TOUT LE RESTE PROTÉGÉ ==========
                        .anyRequest().authenticated()) // Tout le reste protégé

                // Configuration des sessions
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))

                // Désactiver CSRF pour API REST
                .csrf(AbstractHttpConfigurer::disable)

                // Configuration CORS pour Railway
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Gestion des exceptions d'authentification
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler()))

                .build();
    }

    /**
     * Point d'entrée personnalisé pour les erreurs d'authentification
     * Retourne 401 au lieu de 403 pour les utilisateurs non authentifiés
     */
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("Utilisateur non authentifié");
        };
    }

    /**
     * Gestionnaire personnalisé pour les erreurs d'accès
     * Retourne 403 seulement pour les vrais problèmes d'autorisation
     */
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("Accès refusé");
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configuration CORS pour Railway et développement
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://*.up.railway.app",    // Tous les sous-domaines Railway
                "https://fishon-production.up.railway.app",  // Votre domaine spécifique
                "http://localhost:*",          // Développement local
                "http://127.0.0.1:*"          // Développement local
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applique à toutes les routes
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}