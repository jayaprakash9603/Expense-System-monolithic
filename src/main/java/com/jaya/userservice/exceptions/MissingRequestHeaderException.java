package com.jaya.userservice.exceptions;

public class MissingRequestHeaderException extends RuntimeException{

   public MissingRequestHeaderException(String message) {
        super(message);
    }
}
