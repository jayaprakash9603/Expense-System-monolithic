package com.jaya.userservice.exceptions;

public class BadCredentialsException extends RuntimeException  {
    public BadCredentialsException(String message) {
        super(message);
    }

}
