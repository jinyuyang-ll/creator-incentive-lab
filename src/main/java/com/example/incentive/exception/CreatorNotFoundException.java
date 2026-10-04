package com.example.incentive.exception;

public class CreatorNotFoundException extends RuntimeException {
    public CreatorNotFoundException(long creatorId) {
        super("Creator not found: " + creatorId);
    }
}
