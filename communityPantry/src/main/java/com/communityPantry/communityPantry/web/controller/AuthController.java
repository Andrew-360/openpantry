package com.communityPantry.communityPantry.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.communityPantry.communityPantry.service.UserService;
import com.communityPantry.communityPantry.web.interfaces.AuthControllerInterface;
import com.communityPantry.communityPantry.dto.User.RegisterRequest;
import com.communityPantry.communityPantry.dto.User.LoginResponse;
import com.communityPantry.communityPantry.dto.User.LoginRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerInterface {

    UserService userService;

    // dependency injection of the user service
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // register a new user
    @Override
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest) {
        String response = userService.registerUser(registerRequest);
        return ResponseEntity.ok(response);
    }

    // log in
    // return the login response which contains the JWT token and user details if
    // login is successful
    @Override
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.loginUser(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

}