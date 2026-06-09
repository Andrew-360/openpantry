package com.communityPantry.communityPantry.exception;

public class UserAlreadyExistsException extends RuntimeException {
    // this exception is thrown when a user tries to register with a username that
    // already exists in the database

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
