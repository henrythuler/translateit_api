package com.bureau.translateit.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDocumentCsvException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDocumentCsvException(InvalidDocumentCsvException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTranslatorCsvException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTranslatorCsvException(InvalidTranslatorCsvException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TranslatorNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTranslatorNotFoundException(TranslatorNotFoundException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDocumentNotFoundException(DocumentNotFoundException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoRecordsFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoRecordsFoundException(NoRecordsFoundException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyUsedException(EmailAlreadyUsedException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity <Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity <Map<String, String>> handlePropertyReferenceException(PropertyReferenceException e){
        Map<String, String> error = new HashMap<>();
        error.put("message", "Sort query not valid: " + e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity <Map<String, String>> handleConstraintViolationException(ConstraintViolationException e){
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = ((FieldError) error).getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OpenAiException.class)
    public ResponseEntity <Map<String, String>> handleOpenAiException(OpenAiException e){
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
