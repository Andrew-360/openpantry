package com.communityPantry.communityPantry.exception;

public class EntityNotFoundException extends RuntimeException {
    // this exception is thrown when an entity is not found in the database

    public EntityNotFoundException(String message) {
        super(message);
    }
}
