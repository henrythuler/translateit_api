package com.bureau.translateit.exceptions;

public class InvalidCsvException extends RuntimeException {
    public InvalidCsvException() {
        super("Invalid CSV. File headers should be = id(when updating),subject,content,locale(optional),author");
    }
}
