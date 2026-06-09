package com.communityPantry.communityPantry.exception;

public class DuplicateEntityException extends RuntimeException {
    // this exception is thrown when an entity already exists in the database
    public DuplicateEntityException(String message) {
        super(message);
    }
}