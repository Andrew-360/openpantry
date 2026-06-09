package com.communityPantry.communityPantry.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * This class handles unauthorized access attempts to protected resources in the
 * application.
 * It implements the AuthenticationEntryPoint interface from Spring Security,
 * which is called whenever
 * an unauthenticated user tries to access a secured HTTP endpoint.
 * The main responsibility is to send a 401 Unauthorized error response when
 * authentication fails or is missing.
 */

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        // This method is triggered anytime an unauthenticated user requests a secured
        // HTTP resource
        // and an AuthenticationException is thrown.
        // It sends a 401 Unauthorized error response to the client, indicating that
        // authentication is required.
        // The error message can be customized as needed.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }

}
