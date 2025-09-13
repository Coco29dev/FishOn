package com.FishOn.FishOn.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;
import java.util.HashMap;

@RestController
public class HealthController {

    @Autowired(required = false)
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Vérification basique
            response.put("status", "UP");
            response.put("service", "FishOn API");
            response.put("timestamp", java.time.Instant.now().toString());
            response.put("version", "1.0.0");

            // Test connexion base de données (optionnel)
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    response.put("database", "UP");
                } catch (Exception e) {
                    response.put("database", "DOWN");
                    response.put("database_error", e.getMessage());
                    // Ne pas retourner 500, Railway préfère 200 avec statut dans le body
                }
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("timestamp", java.time.Instant.now().toString());

            // Railway préfère généralement 200 même en erreur, avec le statut dans le body
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/")
    public Map<String, String> root() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "FishOn API is running");
        response.put("version", "1.0.0");
        response.put("status", "healthy");
        return response;
    }
}