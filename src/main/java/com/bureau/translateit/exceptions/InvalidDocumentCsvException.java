package com.bureau.translateit.exceptions;

public class InvalidCsvException extends RuntimeException {
    public InvalidDocumentCsvException(String entity) {
        super("Invalid CSV.\nCreation CSV File content = subject;content;locale(optional);author\nUpdate CSV File Content");
    }
}
