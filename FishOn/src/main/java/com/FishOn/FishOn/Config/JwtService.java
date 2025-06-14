package com.FishOn.FishOn.Config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.FishOn.FishOn.Model.UserModel;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    // Configuration via application.properties (optionnel pour plus tard)
    @Value("${jwt.secret-key:myFishOnSecretKeyForJWTSigningMustBeLongEnoughForHS256Algorithm123456789}")
    private String secretKey;
    
    @Value("${jwt.expiration-time:86400000}") // 24h par défaut
    private int expirationTime;

    // ========= 1. CONFIGURATION =========
    private String getSecretKey() {
        return secretKey;
    }
    
    private int getExpirationTime() {
        return expirationTime;
    }

    // ========= 2. GÉNÉRATION =========
    
    /**
     * Génère un JWT pour un utilisateur donné
     * @param user L'utilisateur pour lequel générer le token
     * @return Le JWT sous forme de string
     */
    public String generateToken(UserModel user) {
        // Créer les claims personnalisés (données dans le token)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("userName", user.getUserName());
        
        return createToken(claims, user.getEmail());
    }
    
    /**
     * Crée le token JWT avec les claims et le subject
     * @param claims Les données personnalisées à inclure
     * @param subject Le sujet du token (généralement l'email)
     * @return Le JWT généré
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)                              // Données personnalisées
                .setSubject(subject)                            // Email de l'utilisateur
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Date de création
                .setExpiration(new Date(System.currentTimeMillis() + getExpirationTime())) // Date d'expiration
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Signature avec clé secrète
                .compact();                                     // Génération du token final
    }
    
    /**
     * Convertit la clé secrète string en SecretKey pour la signature
     * @return La clé de signature
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(getSecretKey().getBytes());
    }
}