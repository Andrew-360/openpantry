package com.communityPantry.communityPantry.exception;

public class AlreadyExpiredException extends RuntimeException {
    // this exception is thrown when a resource has already expired
    public AlreadyExpiredException(String message) {
        super(message);
    }
}