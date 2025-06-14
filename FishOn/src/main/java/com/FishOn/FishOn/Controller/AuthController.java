package com.FishOn.FishOn.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.FishOn.FishOn.DTO.Auth.*;
import com.FishOn.FishOn.Exception.FishOnException.*;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.AuthService;
import com.FishOn.FishOn.Config.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController // Spring va automatiquement JSON
@RequestMapping("/api/auth") // Préfixe URL
public class AuthController {

    @Autowired // Injection automatique
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO registerRequest)
            throws EmailAlreadyExists, UserAlreadyExists {

        UserModel user = new UserModel(
                registerRequest.getUserName(),
                registerRequest.getEmail(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getAge(),
                registerRequest.getPassword(),
                registerRequest.getProfilePicture()
        );

        // Appel service authentification pour enregistrement utilisateur bdd
        UserModel newUser = authService.register(user);

        return new RegisterResponseDTO(
                newUser.getId(),
                newUser.getUserName(),
                newUser.getEmail(),
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getAge(),
                newUser.getProfilePicture(),
                newUser.getCreatedAt()
        );
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest) 
            throws UserNotFoundByEmail, InvalidPassword {
    
        // Spring Security vérifié identifiants, si incorrects exception levée automatiquement
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                )
            );
        
        // Stockage authentification réussie dans le contexte Spring Security session courante
        SecurityContextHolder.getContext().setAuthentication(authentication);
    
        // Récupération directe depuis l'Authentication
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserModel user = userDetails.getUser();

        return new LoginResponseDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getProfilePicture()
        );
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // Nettoyage conetxte Spring Security
        SecurityContextHolder.clearContext();
        return "Déconnexion réussie";
    }
}