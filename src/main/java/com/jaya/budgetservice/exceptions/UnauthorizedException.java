package com.jaya.budgetservice.exceptions;

/**
 * Thrown when authentication fails (e.g., invalid or expired token).
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}