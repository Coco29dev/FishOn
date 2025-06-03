package com.example.API.Peche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.API.Peche.model.Catch;
import java.util.List;

public interface CatchRepository extends JpaRepository<Catch, Long> {

    // Signature méthode liste prise utilisateur
    List<Catch> findByUserId(Long id);
    // SQL généré
    // SELECT * FROM catches WHERE user_id = id

    // Signature méthode liste par nom du poisson
    List<Catch> findByFishName(String fishName);
    // SQL généré
    // SELECT * FROM fish_name = fishName

    // Signature méthode liste par lieu
    List<Catch> findByLocation(String location);
    // SQL généré
    // SELECT * FROM catches WHERE location = location
}
