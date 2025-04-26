package com.bureau.translateit.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("The email: " + email + " is already in use.");
    }
}
