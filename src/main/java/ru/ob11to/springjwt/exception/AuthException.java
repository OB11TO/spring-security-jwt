package ru.ob11to.springjwt.exception;

public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }
}
