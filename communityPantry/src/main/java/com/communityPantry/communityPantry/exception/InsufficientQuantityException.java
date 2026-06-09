package com.communityPantry.communityPantry.exception;

public class InsufficientQuantityException extends RuntimeException {
    // this exception is thrown when there is not enough quantity of a food item available

    public InsufficientQuantityException(String message) {
        super(message);
    }
}