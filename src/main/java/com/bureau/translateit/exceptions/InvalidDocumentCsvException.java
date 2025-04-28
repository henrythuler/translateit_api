package com.bureau.translateit.exceptions;

public class InvalidDocumentCsvException extends RuntimeException {
    public InvalidDocumentCsvException() {
        super("Invalid format: CSV file content should be id(only when uploading for update);subject;content;locale(optional);author");
    }
}
