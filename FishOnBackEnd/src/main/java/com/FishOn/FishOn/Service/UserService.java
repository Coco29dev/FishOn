package com.FishOn.FishOn.Service;

import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Repository.UserRepository;
import com.FishOn.FishOn.Exception.FishOnException.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // Méthode CRUD
    public UserModel createUser(UserModel user) throws EmailAlreadyExists, UserAlreadyExists {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExists(user.getEmail());
        }

        if (userRepository.existsByUserName(user.getUserName())) {
            throw new UserAlreadyExists(user.getUserName());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserModel updateUser(UUID userId, UserModel updatedUser) throws UserNotFoundById , EmailAlreadyExists, UserAlreadyExists {
        // Récupérer l'utilisateur avec son ID, si non trouvé envoie l'exception UserNotFound avec orElseThrow
        UserModel existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        // Premier if : vérification si l'email de base est différente du nouvel email
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            // Deuxième if : vérification si le nouvel email existe déjà en base de donnée
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                // Lance une exception si le nouvel email est déjà existant
                throw new EmailAlreadyExists(updatedUser.getEmail());
            }
        }

        // Premier if : vérification si l'username de base est différent du nouvel username
        if (!existingUser.getUserName().equals(updatedUser.getUserName())) {
            // Deuxième if : vérification si le nouvel username existe déjà en base de donnée
            if (userRepository.existsByUserName(updatedUser.getUserName())) {
                // Lance une exception si le nouvel username est déjà existant
                throw new UserAlreadyExists(updatedUser.getUserName());
            }
        }

        // MAJ les champs modifié obligatoire (email, username)
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUserName(updatedUser.getUserName());

        // MAJ champs optionnels
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setProfilePicture(updatedUser.getProfilePicture());

        // Retourne l'user modifié
        return userRepository.save(existingUser);

    }

    public void deleteUser(UUID userId) throws UserNotFoundById {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));
        userRepository.delete(user);
        System.out.println("L'utilisateur " + userId + " a été supprimé");
    }

    // Méthode repository
    public UserModel getByUserName(String userName) throws UserNotFoundByUserName {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundByUserName(userName));
    }

    public UserModel getByEmail(String email) throws UserNotFoundByEmail {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmail(email));
    }

    public boolean userNameExists(String userName) throws UserNotFoundByUserName {
        if (!userRepository.existsByUserName(userName)) {
            throw new UserNotFoundByUserName(userName);
        }
        return true;
    }

    public boolean emailExists(String email) throws UserNotFoundByEmail {
        if (!userRepository.existsByEmail(email)) {
            throw new UserNotFoundByEmail(email);
        }
        return true;
    }
}