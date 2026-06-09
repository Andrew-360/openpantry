package com.communityPantry.communityPantry.service;

import com.communityPantry.communityPantry.exception.InvalidCredentialsException;
import com.communityPantry.communityPantry.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.communityPantry.communityPantry.repository.UserRepository;
import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.domain.enums.SystemRole;
import com.communityPantry.communityPantry.dto.User.LoginRequest;
import com.communityPantry.communityPantry.dto.User.LoginResponse;
import com.communityPantry.communityPantry.dto.User.RegisterRequest;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.communityPantry.communityPantry.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

        // mock the dependencies of the UserService class, such as the UserRepository,
        // PasswordEncoder, and JwtService, using Mockito annotations like @Mock and
        // @InjectMocks

        @Mock
        private UserRepository userRepository;
        @Mock
        private UserProfileRepository userProfileRepository;
        @Mock
        private PasswordEncoder passwordEncoder;
        @Mock
        private JwtService jwtService;
        @Mock
        private AuthenticationManager authenticationManager;

        @InjectMocks
        private UserService userService;

        // unit tests for registerUser method of the UserService class, testing
        // different scenarios such as
        // successful registration,
        // registration with an existing username, and
        // invalid inputs dont need to be tested here as they are handled by the
        // validation annotations in the RegisterRequest DTO, and the AuthController
        // uses the @Valid annotation, but will be tested in the integration tests for
        // AuthController

        // should return a succes string when the registration is successful
        @Test
        void shouldReturnSuccessStringWhenRegistrationIsSuccessful() {
                // test the registerUser method of the UserService class, by creating a
                // RegisterRequest
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setUsername("testuser");
                registerRequest.setRawPassword("testpassword");
                registerRequest.setLocation("testlocation");
                registerRequest.setEmail("testuser@gmail.com");
                registerRequest.setPhoneNumber("1234567890");

                // mock the behavior of the UserRepository to return false when checking if the
                // username already exists
                when(userRepository.existsByUsername("testuser")).thenReturn(false);
                // mock the behavior of the PasswordEncoder to return an encoded password when
                // encoding the raw password
                when(passwordEncoder.encode("testpassword")).thenReturn("encodedpassword");
                // mock the behavior of the UserRepository to return a saved user when saving a
                // new user
                User savedUser = new User();
                savedUser.setId(1l);
                savedUser.setUsername("testuser");
                savedUser.setPassword("encodedpassword");
                savedUser.setRole(SystemRole.USER);
                when(userRepository.save(any(User.class))).thenReturn(savedUser);
                // mock the behavior of the UserProfileRepository to return a saved user profile
                UserProfile savedUserProfile = new UserProfile();
                savedUserProfile.setId(1l);
                savedUserProfile.setUser(savedUser);
                savedUserProfile.setLocation("testlocation");
                savedUserProfile.setEmail("testuser@gmail.com");
                when(userProfileRepository.save(any(UserProfile.class))).thenReturn(savedUserProfile);

                // call the registerUser method of the UserService class with the
                // RegisterRequest
                String result = userService.registerUser(registerRequest);
                // assert that the result is the expected success string
                assertEquals("User registered successfully! Please log in to continue.", result);
                // verify that the UserRepository's existsByUsername method was called with the
                // correct username
                verify(userRepository).existsByUsername("testuser");
                // verify that the PasswordEncoder's encode method was called with the correct
                // raw password
                verify(passwordEncoder).encode("testpassword");
                // verify that the UserRepository's save method was called with a User object
                // that has the correct username, encoded password, and role
                verify(userRepository).save(argThat(user -> user.getUsername().equals("testuser")
                                && user.getPassword().equals("encodedpassword") && user.getRole() == SystemRole.USER));
                // verify that the UserProfileRepository's save method was called with a
                // UserProfile object that has the correct user, location, and email
                verify(userProfileRepository).save(argThat(userProfile -> userProfile.getUser().equals(savedUser)
                                && userProfile.getLocation().equals("testlocation")
                                && userProfile.getEmail().equals("testuser@gmail.com")));
        }

        // registration with an existing username should throw a
        // UserAlreadyExistsException with
        // the message "Username 'existingusername' is already taken."
        @Test
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
                // test the registerUser method of the UserService class, by creating a
                // RegisterRequest
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setUsername("existingusername");
                registerRequest.setRawPassword("testpassword");
                registerRequest.setLocation("testlocation");
                registerRequest.setEmail("testuser@gmail.com");
                registerRequest.setPhoneNumber("1234567890");

                // mock the behavior of the UserRepository to return false when checking if the
                // username already exists
                when(userRepository.existsByUsername("existingusername")).thenReturn(true);

                // call the registerUser method and assert that it throws a
                // UserAlreadyExistsException
                // with the expected message
                UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                                () -> userService.registerUser(registerRequest));
                assertEquals("Username 'existingusername' is already taken.", exception.getMessage());
                // verify that the UserRepository's existsByUsername method was called with the
                // correct username
                verify(userRepository).existsByUsername("existingusername");
                // verify that the PasswordEncoder's encode method was never called, since the
                // registration should have failed before reaching that point
                verify(passwordEncoder, never()).encode(any(String.class));
                // verify that the UserRepository's save method was never called, since the
                // registration should have failed before reaching that point
                verify(userRepository, never()).save(any(User.class));
                // verify that the UserProfileRepository's save method was never called, since
                // the registration should have failed before reaching that point
                verify(userProfileRepository, never()).save(any(UserProfile.class));
        }

        // registration with an existing email should throw a
        // UserAlreadyExistsException with
        // the message "Email 'existingemail@gmail.com' is already taken."
        @Test
        void shouldThrowExceptionWhenEmailAlreadyExists() {
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setUsername("newusername");
                registerRequest.setRawPassword("testpassword");
                registerRequest.setLocation("testlocation");
                registerRequest.setEmail("existingemail@gmail.com");
                registerRequest.setPhoneNumber("1234567890");

                // mock the behavior of the UserProfileRepository to return true when checking
                // if the email already exists
                when(userProfileRepository.existsByEmail("existingemail@gmail.com")).thenReturn(true);

                // call the registerUser method and assert that it throws a
                // UserAlreadyExistsException
                // with the expected message
                UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                                () -> userService.registerUser(registerRequest));
                assertEquals("Email 'existingemail@gmail.com' is already taken.", exception.getMessage());
                // verify that the UserProfileRepository's existsByEmail method was called with
                // the correct email
                verify(userProfileRepository).existsByEmail("existingemail@gmail.com");
                // verify that the UserRepository's existsByUsername method was never called,
                // since the registration should have failed before reaching that point
                verify(userRepository, never()).existsByUsername(any(String.class));
                // verify that the PasswordEncoder's encode method was never called, since the
                // registration should have failed before reaching that point
                verify(passwordEncoder, never()).encode(any(String.class));
                // verify that the UserRepository's save method was never called, since the
                // registration should have failed before reaching that point
                verify(userRepository, never()).save(any(User.class));
                // verify that the UserProfileRepository's save method was never called, since
                // the registration should have failed before reaching that point
                verify(userProfileRepository, never()).save(any(UserProfile.class));
        }

        // unit tests for loginUser method of the UserService class, testing
        // different scenarios such as
        // successful login,
        // login with incorrect credentials,
        // invalid inputs dont need to be tested here as they are handled by the
        // validation annotations in the LoginRequest DTO, and the AuthController uses
        // the @Valid annotation, but will be tested in the integration tests for
        // AuthController

        // test for a successful login
        // should return a LoginResponse object with the expected user details and JWT
        // token
        @Test
        void shouldReturnLoginResponseAndJwtTokenWhenLoginIsSuccessful() {
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsername("testuser");
                loginRequest.setRawPassword("testpassword");

                // mock the authenticated user
                User authenticatedUser = new User();
                authenticatedUser.setId(1l);
                authenticatedUser.setUsername("testuser");
                authenticatedUser.setPassword("encodedpassword");
                authenticatedUser.setRole(SystemRole.USER);
                // mock the authenitcation and authenticationManager steps
                Authentication authentication = mock(Authentication.class);
                when(authentication.getPrincipal()).thenReturn(authenticatedUser);
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication);

                // mock the user profile associated with the authenticated user
                UserProfile userProfile = new UserProfile();
                userProfile.setId(1l);
                userProfile.setUser(authenticatedUser);
                userProfile.setLocation("testlocation");
                userProfile.setEmail("testuser@gmail.com");
                userProfile.setPhoneNumber("1234567890");
                // mock the userProfileRepository to return the user profile when finding by
                // user id
                when(userProfileRepository.findByUserId(1l)).thenReturn(Optional.of(userProfile));

                // mock the JwtService to return a JWT token when generating a token for the
                // authenticated user
                when(jwtService.generateToken(authenticatedUser)).thenReturn("testjwttoken");
                when(jwtService.getUsernameFromToken("testjwttoken")).thenReturn("testuser");
                when(jwtService.getRoleFromToken("testjwttoken")).thenReturn("USER");

                // call the loginUser method of the UserService class with the LoginRequest
                LoginResponse loginResponse = userService.loginUser(loginRequest);

                // assert the loginResponse has the expected fields
                assertEquals("testuser", loginResponse.getUsername());
                assertEquals("USER", loginResponse.getSystemRole());
                assertEquals("testjwttoken", loginResponse.getToken());
                assertEquals("testlocation", loginResponse.getLocation());
                assertEquals("testuser@gmail.com", loginResponse.getEmail());
                assertEquals("1234567890", loginResponse.getPhoneNumber());

                // verify the mock methods were called with the expected arguments
                verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
                verify(userProfileRepository).findByUserId(1l);
                verify(jwtService).generateToken(authenticatedUser);
                verify(jwtService).getUsernameFromToken("testjwttoken");
                verify(jwtService).getRoleFromToken("testjwttoken");
        }

        // login with incorrect credentials
        // should throw an InvalidCredentialsException with the message "Bad
        // credentials"
        @Test
        void shouldThrowExceptionWhenLoginWithIncorrectCredentials() {
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsername("testuser");
                loginRequest.setRawPassword("wrongpassword");

                // mock the authenticationManager to throw a BadCredentialsException when trying
                // to
                // authenticate with incorrect credentials
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenThrow(new InvalidCredentialsException("Bad credentials"));

                // call the loginUser method and assert that it throws an
                // InvalidCredentialsException with
                // the expected message
                InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                                () -> userService.loginUser(loginRequest));
                assertEquals("Bad credentials", exception.getMessage());

                // verify that the authenticationManager's authenticate method was called with a
                // UsernamePasswordAuthenticationToken that has the correct username and
                // incorrect raw
                // password
                verify(authenticationManager).authenticate(argThat(token -> token.getPrincipal().equals("testuser")
                                && token.getCredentials().equals("wrongpassword")));
                // verify that the userProfileRepository's findByUserId method was never called,
                // since the authentication should have failed before reaching that point
                verify(userProfileRepository, never()).findByUserId(any(Long.class));
                // verify that the JwtService's generateToken method was never called, since the
                // authentication should have failed before reaching that point
                verify(jwtService, never()).generateToken(any(User.class));
                verify(jwtService, never()).getUsernameFromToken(any(String.class));
                verify(jwtService, never()).getRoleFromToken(any(String.class));
        }
}
