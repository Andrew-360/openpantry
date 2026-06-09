package com.communityPantry.communityPantry.exception;

public class InvalidCredentialsException extends RuntimeException {
    // this exception is thrown when a user tries to log in with invalid credentials

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
