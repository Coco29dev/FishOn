package com.FishOn.FishOn.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    /**
     * Route racine - redirige vers login
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/HTML/login.html";
    }

    /**
     * Route pour toutes les pages HTML
     * Permet d'accéder directement aux pages sans extension dans l'URL
     */
    @GetMapping("/login")
    public String login() {
        return "redirect:/HTML/login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "redirect:/HTML/register.html";
    }

    @GetMapping("/feed")
    public String feed() {
        return "redirect:/HTML/feed.html";
    }

    @GetMapping("/profile")
    public String profile() {
        return "redirect:/HTML/profile.html";
    }

    @GetMapping("/journal")
    public String journal() {
        return "redirect:/HTML/journal.html";
    }
}