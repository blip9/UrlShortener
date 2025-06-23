package com.url.urlshortener.controllers;

import com.url.urlshortener.dtos.LoginRequest;
import com.url.urlshortener.dtos.RegisterRequest;
import com.url.urlshortener.models.User;
import com.url.urlshortener.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/public/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setRole("ROLE_USER");
        try {
            userService.registerUser(user);
            return new ResponseEntity<>("User creation successful.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("User creation failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/public/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(userService.authenticateUser(loginRequest), HttpStatus.OK);
    }
}
