package com.FishOn.FishOn.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.FishOn.FishOn.Exception.FishOnException.EmailAlreadyExists;
import com.FishOn.FishOn.Exception.FishOnException.UserAlreadyExists;
import com.FishOn.FishOn.Model.UserModel;
import com.FishOn.FishOn.Service.AuthService;
import com.FishOn.FishOn.Service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserModel register(@Valid @RequestBody UserModel user) throws EmailAlreadyExists, UserAlreadyExists // @RequestBody = Conversion JSON -> User object
    {
        UserModel newUser = authService.register(user);
        return newUser;
    }
}
