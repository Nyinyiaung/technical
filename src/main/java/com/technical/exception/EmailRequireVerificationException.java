package com.technical.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class EmailRequireVerificationException extends RuntimeException {
    public EmailRequireVerificationException(String message) {
        super(message);
    }
}
