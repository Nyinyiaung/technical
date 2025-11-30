package com.technical.exception;

import jakarta.mail.MessagingException;

public class EmailSendFailedException extends RuntimeException {
    public EmailSendFailedException(String message, MessagingException e) {
        super(message, e);
    }
}