package com.technical.dto.common;

import org.springframework.http.HttpStatus;

public record ExceptionConfig(String code, String messageKey, HttpStatus status, boolean logException) {
	public ExceptionConfig(String code, String messageKey, HttpStatus status) {
		this(code, messageKey, status, true);
	}
}