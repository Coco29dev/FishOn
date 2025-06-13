package com.FishOn.FishOn.Service;

import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.UserService;
import com.FishOn.FishOn.Repository.UserRepository;
import org.springframework.stereotype.Service;
import com.FishOn.FishOn.Exception.FishOnException.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // Méthode Login
    public UserModel login(String email, String password) throws UserNotFoundByEmail, InvalidPassword{
        UserModel existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmail(email));

        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new InvalidPassword();
        }
        return existingUser;
    }

    // Méthode Register
    public UserModel register(UserModel user) throws EmailAlreadyExists, UserAlreadyExists {
        return userService.createUser(user);
    }

    // Méthode changement de mot de passe
    public void updatePassword(String email, String currentPassword, String newPassword) throws UserNotFoundByEmail, InvalidPassword{
        UserModel existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmail(email));
        if (!passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
            throw new InvalidPassword();
        }
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(existingUser);
        System.out.println("Mot de passe mis à jour pour l'utilisateur : " + email);
    }
}
