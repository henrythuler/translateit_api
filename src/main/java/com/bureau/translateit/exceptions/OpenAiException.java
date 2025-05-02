package com.bureau.translateit.exceptions;

public class OpenAiException extends RuntimeException {
    public OpenAiException(String message) {
        super(message);
    }
}
