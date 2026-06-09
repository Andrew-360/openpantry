package com.communityPantry.communityPantry.exception;

public class AlreadyReservedException extends RuntimeException {
    // this exception is thrown when a resource has already been reserved
    public AlreadyReservedException(String message) {
        super(message);
    }
}