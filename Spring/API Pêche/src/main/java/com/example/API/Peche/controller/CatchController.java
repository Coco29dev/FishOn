package com.example.API.Peche.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.API.Peche.model.Catch;
import com.example.API.Peche.repository.CatchRepository;
import com.example.API.Peche.service.CatchService;

import com.example.API.Peche.model.User;
import com.example.API.Peche.repository.UserRepository;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/catches")
public class CatchController {

    @Autowired // Injection Automatique
    private CatchRepository catchRepository;
    @Autowired // Injection Automatique
    private UserRepository userRepository;
    @Autowired // Injection Automatique
    private CatchService catchService;

    // Méthode affichage toutes les prises
    @GetMapping
    public List<Catch> allCatches() {
        return catchRepository.findAll();
    }

    // Méthode affichage toutes mes prises
    @GetMapping("/my")
    public List<Catch> allMyCatches(Principal principal)
    // Principal principal =  utilisateur courant connecté
    {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        Long userId = user.getId();
        return catchService.getUserCatches(userId);
    }
}