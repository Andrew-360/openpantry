package com.communityPantry.communityPantry.web.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.communityPantry.communityPantry.dto.User.LoginRequest;
import com.communityPantry.communityPantry.dto.User.LoginResponse;
import com.communityPantry.communityPantry.dto.User.RegisterRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public interface AuthControllerInterface {

    @Operation(summary = "Register a new user", description = "Registers a new user and returns a success message.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error: invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest);

    @Operation(summary = "User login", description = "Authenticates user and returns JWT token and user details if successful.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Validation error: invalid input"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest);

}