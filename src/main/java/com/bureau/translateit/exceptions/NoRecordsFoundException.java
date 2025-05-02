package com.bureau.translateit.exceptions;

public class NoRecordsFoundException extends RuntimeException {
    public NoRecordsFoundException(String entity) {
        super("No " + entity + " found.");
    }
}
