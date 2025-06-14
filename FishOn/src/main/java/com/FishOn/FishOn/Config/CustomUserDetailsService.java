package com.FishOn.FishOn.Config;

import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.UserService;
import com.FishOn.FishOn.Exception.FishOnException.UserNotFoundByEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserModel user = userService.getByEmail(email);
            return new CustomUserDetails(user);
            
        } catch (UserNotFoundByEmail e) {
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + email, e);
        }
    }
}
