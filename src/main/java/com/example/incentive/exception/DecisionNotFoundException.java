package com.example.incentive.exception;

public class DecisionNotFoundException extends RuntimeException {
    public DecisionNotFoundException(long decisionId) {
        super("Decision not found: " + decisionId);
    }
}
