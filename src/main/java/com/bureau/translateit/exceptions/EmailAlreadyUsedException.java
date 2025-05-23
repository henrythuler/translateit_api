package com.bureau.translateit.exceptions;

import java.util.UUID;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("The email: " + email + " is already in use.");
    }

    public EmailAlreadyUsedException(String email, String name) {
        super("The email: " + email + " is already in use by " + name);
    }
}
