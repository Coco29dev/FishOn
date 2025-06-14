package com.FishOn.FishOn.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.FishOn.FishOn.DTO.Auth.*;
import com.FishOn.FishOn.Exception.FishOnException.*;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.AuthService;

import jakarta.validation.Valid;

@RestController // Combinaison @Controller + @ ResponseBody, indiquant que toutes les méthodes retourne du JSON
@RequestMapping("/api/auth") // Définition préfixe URL endpoints contrôleur
public class AuthController {

    @Autowired // Injection Automatique
    private AuthService authService;


    @PostMapping("/register") // Endpoint inscription
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO registerRequest)
            throws EmailAlreadyExists, UserAlreadyExists
    // @Valid déclenche validation des contraintes définie dans RegisterRequestDTO
    {

        // Conversion données registerRequest en objet UserModel
        UserModel user = new UserModel(
                registerRequest.getUserName(),
                registerRequest.getEmail(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getAge(),
                registerRequest.getPassword(),
                registerRequest.getProfilePicture()
        );

        // Appel de AuthService pour récupérer/utiliser la méthode register
        UserModel newUser = authService.register(user);

        // Transforme newUser en objet RegisterResponseDTO
        RegisterResponseDTO response = new RegisterResponseDTO(
                newUser.getId(),
                newUser.getUserName(),
                newUser.getEmail(),
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getAge(),
                newUser.getProfilePicture(),
                newUser.getCreatedAt()
        );

        return response;
    }

    @PostMapping("/login") // Endpoint connexion
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest)
            throws UserNotFoundByEmail, InvalidPassword
    // @Valid déclenche validation des contraintes définie dans LoginRequestDTO
    {

        // Appel service pour récupérer/utiliser la méthode login avec comme paramètre email + password
        UserModel user = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        // Conversion user en objet LoginResponseDTO
        LoginResponseDTO response = new LoginResponseDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getProfilePicture()
        );

        return response;
    }
}
