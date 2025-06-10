package com.example.API.Peche.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.API.Peche.service.UserService;
import com.example.API.Peche.model.User;

@RestController
@RequestMapping("/api/auth") // Définition préfixe URL classe
public class AuthController {

    @Autowired // Injection Automatique
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) // @RequestBody = Conversion JSON -> User object
    {
        User newUser = userService.createUser(user);
        return newUser;
    }
}
