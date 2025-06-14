package com.FishOn.FishOn.Config;

import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.UserService;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundByEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserModel user = userService.getByEmail(email);

            return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new ArrayList<>())
                .build();
                
        } catch (UserNotFoundByEmail e) {
            // Conversion nécessaire pour Spring Security
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + email, e);
        }
    }
}
