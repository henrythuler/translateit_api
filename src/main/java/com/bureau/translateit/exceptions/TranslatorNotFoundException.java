package com.bureau.translateit.exceptions;

public class TranslatorNotFoundException extends RuntimeException {
    public TranslatorNotFoundException(String email) {
        super("Translator with email: " + email + " not found.");
    }
}
