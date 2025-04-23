package com.bureau.translateit.exceptions;

import java.util.UUID;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String filter) {
        super(filter + " documents not found.");
    }

    public DocumentNotFoundException(UUID id) {
        super("Document with id " + id.toString() + " not found");
    }
}
