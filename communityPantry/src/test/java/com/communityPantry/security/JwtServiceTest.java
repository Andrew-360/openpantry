package com.communityPantry.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.communityPantry.communityPantry.domain.enums.SystemRole;
import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.security.JwtService;

import io.jsonwebtoken.JwtException;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    // unit tests for JwtService

    private JwtService jwtService;

    // need to set up the secretKey and expirationTime for the JwtService before
    // each test
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "testsecretkeytestsecretkeytestsecretkey12");
        ReflectionTestUtils.setField(jwtService, "expirationTime", 60000L);
        // call the init method to initialize the key
        jwtService.init();
    }

    // test for generating a token and validating it
    @Test
    void testGenerateAndValidateToken() {
        // create a user object to generate a token for
        User user = new User();
        user.setUsername("testuser");
        user.setRole(SystemRole.USER);

        // generate a token for the user
        String token = jwtService.generateToken(user);

        // validate the generated token
        assertTrue(jwtService.validateToken(token));
        assertEquals("testuser", jwtService.getUsernameFromToken(token));
        assertEquals("USER", jwtService.getRoleFromToken(token));
    }

    // test for validating an invalid token
    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalidToken";
        assertFalse(jwtService.validateToken(invalidToken));
    }

    // test for expired tokens by setting very short expiration time
    @Test
    void testValidateExpiredToken() throws InterruptedException {
        // set a very short expiration time for testing
        ReflectionTestUtils.setField(jwtService, "expirationTime", 1000L);
        // create a user object to generate a token for
        User user = new User();
        user.setUsername("testuser");
        user.setRole(SystemRole.USER);

        // generate a token for the user
        String token = jwtService.generateToken(user);

        // wait for the token to expire
        Thread.sleep(1500L);

        // validate the expired token
        assertFalse(jwtService.validateToken(token));
    }

    // test for validating a token with an invalid signature
    @Test
    void testValidateTokenWithInvalidSignature() {
        // create a user object to generate a token for
        User user = new User();
        user.setUsername("testuser");
        user.setRole(SystemRole.USER);

        // generate a token for the user
        String token = jwtService.generateToken(user);

        // modify the token to make it invalid
        String invalidToken = token.substring(0, token.length() - 1) + "x";

        // validate the modified token
        assertFalse(jwtService.validateToken(invalidToken));
    }

    // test for validating a token with an empty string
    @Test
    void testValidateEmptyToken() {
        String emptyToken = "";
        assertFalse(jwtService.validateToken(emptyToken));
    }

    // test for getting the username and role from a valid token
    @Test
    void testGetUsernameAndRoleFromToken() {
        // create a user object to generate a token for
        User user = new User();
        user.setUsername("testuser");
        user.setRole(SystemRole.USER);

        // generate a token for the user
        String token = jwtService.generateToken(user);

        // get the username and role from the token
        assertEquals("testuser", jwtService.getUsernameFromToken(token));
        assertEquals("USER", jwtService.getRoleFromToken(token));
    }

    // test for getting the username and role from an invalid token
    @Test
    void testGetUsernameAndRoleFromInvalidToken() {
        String token = "invalidToken";
        assertFalse(jwtService.validateToken(token));

        // getting username and role from an invalid token should throw an exception
        try {
            jwtService.getUsernameFromToken(token);
        } catch (Exception e) {

            assertTrue(e instanceof JwtException);
        }

        try {
            jwtService.getRoleFromToken(token);
        } catch (Exception e) {
            assertTrue(e instanceof JwtException);
        }
    }

}
