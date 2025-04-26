package com.bureau.translateit.exceptions;

import java.util.UUID;

public class TranslatorNotFoundException extends RuntimeException {
    public TranslatorNotFoundException(String email) {
        super("Translator with email: " + email + " not found.");
    }

    public TranslatorNotFoundException(UUID id) {
        super("Translator with id: " + id + " not found.");
    }
}
