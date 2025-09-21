package com.FishOn.FishOn.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
     * Routes pour toutes les pages HTML
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

    /**
     * Gestion des routes SPA - important pour Railway
     * Toutes les routes non-API retournent index.html
     */
    @RequestMapping(value = {
            "/HTML/{path:^(?!.*\\.).*$}",
            "/CSS/{path:^(?!.*\\.).*$}",
            "/JS/{path:^(?!.*\\.).*$}",
            "/IMG/{path:^(?!.*\\.).*$}"
    })
    public String forward() {
        return "forward:/";
    }

    /**
     * Fallback pour toutes les autres routes non-API
     */
    @RequestMapping(value = "/{path:^(?!api).*$}")
    public String redirect() {
        return "redirect:/";
    }
}