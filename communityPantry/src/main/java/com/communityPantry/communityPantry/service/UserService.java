package com.communityPantry.communityPantry.service;

import com.communityPantry.communityPantry.dto.User.RegisterRequest;
import com.communityPantry.communityPantry.exception.EntityNotFoundException;
import com.communityPantry.communityPantry.exception.UserAlreadyExistsException;
import com.communityPantry.communityPantry.dto.User.LoginResponse;
import com.communityPantry.communityPantry.dto.User.LoginRequest;
import com.communityPantry.communityPantry.mapper.UserMapper;
import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import com.communityPantry.communityPantry.repository.UserRepository;
import com.communityPantry.communityPantry.security.JwtService;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.communityPantry.communityPantry.domain.enums.SystemRole;

@Service
public class UserService {
    // handle the business logic of user management, such as registraiion and login

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // dependency injection
    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository,
            PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;

    }

    // method to register a new user
    public String registerUser(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String rawPassword = registerRequest.getRawPassword();
        String location = registerRequest.getLocation();
        String email = registerRequest.getEmail();
        String phoneNumber = registerRequest.getPhoneNumber();

        // check if the email is already taken
        if (userProfileRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email '" + email + "' is already taken.");
        }
        // check if the username is already taken
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username '" + username + "' is already taken.");
        }

        // create a new user and save it to the database
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword)); // encode the password before saving it to the database
        // all the new users will have the same system role of USER
        user.setRole(SystemRole.USER);
        User savedUser = userRepository.save(user);

        // create a new user profile and save it to the database
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(savedUser);
        // set the location and email for the user profile
        userProfile.setLocation(location);
        userProfile.setEmail(email);
        userProfile.setPhoneNumber(phoneNumber);
        userProfileRepository.save(userProfile);

        return "User registered successfully! Please log in to continue.";
    }

    // method to login a user
    // return the loginResponse dto which contains the user details and the jwt
    // token
    public LoginResponse loginUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getRawPassword()));

        // get the authenticated User
        User authenticatedUser = (User) authentication.getPrincipal();

        // get the userprofile from the database using the user id
        UserProfile userProfile = userProfileRepository.findByUserId(authenticatedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));

        // generate a JWT token for the authenticated user
        String token = jwtService.generateToken(authenticatedUser);

        // get the username and system role from the token
        String username = jwtService.getUsernameFromToken(token);
        String role = jwtService.getRoleFromToken(token);

        return UserMapper.toLoginResponse(userProfile, username, role, token);
    }
}
