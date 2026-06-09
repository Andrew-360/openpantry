package com.communityPantry.communityPantry.exception;

public class InvalidStatusException extends RuntimeException {
    // this exception is thrown when a resource is in an invalid status for the
    // requested action
    public InvalidStatusException(String message) {
        super(message);
    }
}