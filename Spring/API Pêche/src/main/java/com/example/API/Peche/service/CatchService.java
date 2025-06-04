package com.example.API.Peche.service;

// Annotation pour l'injection automatique des dépendances Spring
import org.springframework.beans.factory.annotation.Autowired;
// Annotation pour marquer une classe comme service (logique métier)
import org.springframework.stereotype.Service;
// Interface Java pour manipuler des listes d'objets
import java.util.List;

import com.example.API.Peche.repository.CatchRepository;
import com.example.API.Peche.repository.UserRepository;

import com.example.API.Peche.model.Catch;
import com.example.API.Peche.model.User;

@Service // Dit à Spring classe contenant logique métier
public class CatchService {

    @Autowired // Injection automatique
    private CatchRepository catchRepository;

    @Autowired // Injection automatique
    private UserRepository userRepository;

    // Méthode ajouter prise
    public Catch addCatches(Long userId, String fishName, Double weight, String location) {

        // Vérification existance user_id
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utilisateur inexistant");
        }

        // Récupération objet User complet
        User user = userRepository.findById(userId).orElse(null);

        // Création objet Catch
        Catch newCatch = new Catch(fishName, weight, location, user);

        // Sauvegarde prise
        Catch savedCatch = catchRepository.save(newCatch);
        return savedCatch;
    }

    // Méthode récupération prise utilisateur
    public List<Catch> getUserCatches(Long userId) {
        return catchRepository.findByUserId(userId);
    }

    // Méthode récupération prise par nom de poisson
    public List<Catch> getCatchesByName(String fishName) {
        // Vérification paramètres valide
        if (fishName == null || fishName.trim().isEmpty())
        // .trim() = Supprime les espaces début et fin
        // .isEmpty() = Vérification string est vide 
        {
            throw new RuntimeException("Nom de poisson inconnue");
        } else {
            return catchRepository.findByFishName(fishName);
        }
    }
}
