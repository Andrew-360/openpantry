package com.communityPantry.communityPantry.web.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.communityPantry.communityPantry.repository.UserRepository;
import com.communityPantry.communityPantry.dto.User.LoginRequest;
import com.communityPantry.communityPantry.dto.User.RegisterRequest;
import com.communityPantry.communityPantry.repository.UserProfileRepository;

import tools.jackson.databind.ObjectMapper;

// Integration tests, so i need to set it up using the full Spring context, and use MockMvc to perform HTTP requests to the controller endpoints
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    // integration tests for the AuthController, testing the register and login
    // endpoints
    // using MockMvc to perform HTTP requests to the controller endpoints and verify
    // the responses

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    // clean up the database before each test to ensure test isolation
    @BeforeEach
    void cleanDatabase() {
        userProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    // test for registering a valid new user
    @Test
    void registerValidNewUserReturnsOk() throws Exception {
        // create a register request object with valid data
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setRawPassword("testpassword");
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setPhoneNumber("1234567890");
        registerRequest.setLocation("Birmingham");

        // perform a POST request to the /api/auth/register endpoint with the register
        // request data
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully! Please log in to continue."));

        // verify that the user was saved to the database
        assert (userRepository.existsByUsername("testuser"));
        assert (userProfileRepository.existsByUserUsername("testuser"));
    }

    // test for registering a user with an existing username, should return a bad
    // request error
    @Test
    void registerExistingUsernameReturnsBadRequest() throws Exception {
        // first a vaild registration with the username "testuser"
        registerValidNewUserReturnsOk();

        // then try to register another user with the same username "testuser"
        // create a register request object with a username that already exists in the
        // database
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setRawPassword("testpassword1");
        registerRequest.setEmail("testuser1@example.com");
        registerRequest.setPhoneNumber("11234567890");
        registerRequest.setLocation("Birmingham");

        // perform a POST request to the /api/auth/register endpoint with the register
        // request data, should return a bad request error because the username already
        // exists
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Username 'testuser' is already taken."));
    }

    // test a valid login, should return the login response with the JWT token
    @Test
    void loginValidUserReturnsLoginResponse() throws Exception {
        // first register a valid user to log in with
        registerValidNewUserReturnsOk();

        // create a login request object with the username and password of the
        // registered user
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setRawPassword("testpassword");

        // perform a POST request to the /api/auth/login endpoint with the login request
        // data, should return the login response with the JWT token and user details
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.systemRole").value("USER"));
    }

    // test for logging in with an invalid username, should return an unauthorized
    // error
    @Test
    void loginInvalidUsernameReturnsUnauthorized() throws Exception {
        // first register a valid user to log in with
        registerValidNewUserReturnsOk();

        // create a login request object with the username and password of the
        // registered user, but with an invalid username
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrongUser");
        loginRequest.setRawPassword("testpassword");

        // perform a POST request to the /api/auth/login endpoint with the login request
        // data, should return the login response with the JWT token and user details
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    // test for logging in with an invalid password, should return an unauthorized
    // error
    @Test
    void loginInvalidPasswordReturnsUnauthorized() throws Exception {
        // first register a valid user to log in with
        registerValidNewUserReturnsOk();

        // create a login request object with the username and password of the
        // registered user, but with an invalid password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setRawPassword("wrongPassword");

        // perform a POST request to the /api/auth/login endpoint with the login request
        // data, should return the login response with the JWT token and user details
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    // tests for validation of the register and login requests
    // e.g. missing username
    @Test
    void registerMissingUsernameReturnsBadRequest() throws Exception {
        // create a register request object with missing username
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setRawPassword("testpassword");
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setPhoneNumber("1234567890");
        registerRequest.setLocation("Birmingham");

        // perform a POST request to the /api/auth/register endpoint with the register
        // request data, should return a bad request error because the username is
        // missing
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.validationErrors.username").exists());
    }

    @Test
    void loginMissingUsernameReturnsBadRequest() throws Exception {
        registerValidNewUserReturnsOk();

        // create a login request object with missing username
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setRawPassword("testpassword");

        // perform a POST request to the /api/auth/login endpoint with the login request
        // data, should return a bad request error because the username is missing
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.validationErrors.username").exists());
    }

    // tests for the size validation of the username and password fields in the
    // register and login requests
    // just username for now
    @Test
    void registerUsernameTooShortReturnsBadRequest() throws Exception {
        // create a register request object with a username that is too short
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("ab");
        registerRequest.setRawPassword("testpassword");
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setPhoneNumber("1234567890");
        registerRequest.setLocation("Birmingham");

        // perform a POST request to the /api/auth/register endpoint with the register
        // request data, should return a bad request error because the username is too
        // short
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.validationErrors.username").exists());
    }

    @Test
    void loginUsernameTooShortReturnsBadRequest() throws Exception {
        registerValidNewUserReturnsOk();

        // create a login request object with a username that is too short
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("ab");
        loginRequest.setRawPassword("testpassword");

        // perform a POST request to the /api/auth/login endpoint with the login request
        // data, should return a bad request error because the username is too short
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.validationErrors.username").exists());
    }
}
