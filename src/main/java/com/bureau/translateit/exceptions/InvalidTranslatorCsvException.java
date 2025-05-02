package com.bureau.translateit.exceptions;

public class InvalidTranslatorCsvException extends RuntimeException {
    public InvalidTranslatorCsvException() {
        super("Invalid format: CSV file content should be id(only when uploading for update);name;email;source_language;target_language");
    }
}
