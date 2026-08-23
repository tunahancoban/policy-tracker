package com.tunahancoban.policy_tracker.model.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessValidationException extends RuntimeException {
    private final String fieldName;
    private final HttpStatus status;

    public BusinessValidationException(String fieldName, String message, HttpStatus status) {
        super(message);
        this.fieldName = fieldName;
        this.status = status;
    }

    public BusinessValidationException(String fieldName, String message) {
        this(fieldName, message, HttpStatus.CONFLICT);
    }
}