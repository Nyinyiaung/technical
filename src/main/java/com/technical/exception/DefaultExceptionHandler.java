package com.technical.exception;

import com.technical.config.MessageConfig;
import com.technical.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class DefaultExceptionHandler {

	private final MessageConfig messageConfig;
	private static final String ERROR_RESPONSE_LOG = "Error Response: {}";

	// code
	private static final String SYSTEM_ERROR = "SYSTEM_ERROR";
	private static final String DB_ERROR = "DB_ERROR";
	private static final String USER_NOT_FOUND = "USER_NOT_FOUND";
	private static final String NO_ACCESS = "NO_ACCESS";
	private static final String WRONG_PASSWORD = "WRONG_PASSWORD";
	private static final String USER_EXISTS = "USER_EXISTS";
	private static final String INVALID_PARAMS = "INVALID_PARAMS";
	private static final String EMAIL_ERROR = "EMAIL_ERROR";

	private ResponseEntity<List<ErrorResponse>> buildErrorResponse(
			String code, String internalMessageKey, String exceptionMessage,
			HttpStatus status, Exception e) {

		List<ErrorResponse> responses = new ArrayList<>();
		responses.add(new ErrorResponse(code, messageConfig.getMessage(internalMessageKey), exceptionMessage));

		log.error(ERROR_RESPONSE_LOG, responses, e);
		return new ResponseEntity<>(responses, new HttpHeaders(), status);
	}

	// ====== Exception handlers ======
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<List<ErrorResponse>> handleRuntime(RuntimeException e) {
		return buildErrorResponse(SYSTEM_ERROR, "system.error", e.getMessage(), HttpStatus.EXPECTATION_FAILED, e);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<List<ErrorResponse>> handleDataIntegrity(DataIntegrityViolationException e) {
		return buildErrorResponse(DB_ERROR, "database.error", e.getMessage(), HttpStatus.CONFLICT, e);
	}

	@ExceptionHandler(ParseException.class)
	public ResponseEntity<List<ErrorResponse>> handleParse(ParseException e) {
		return buildErrorResponse(SYSTEM_ERROR, "date.parsing.error", e.getMessage(), HttpStatus.FORBIDDEN, e);
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<List<ErrorResponse>> handleNullPointer(NullPointerException e) {
		return buildErrorResponse(SYSTEM_ERROR, "system.error", e.getMessage(), HttpStatus.NO_CONTENT, e);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<List<ErrorResponse>> handleUserNotFound(UsernameNotFoundException e) {
		return buildErrorResponse(USER_NOT_FOUND, "username.not.found", e.getMessage(), HttpStatus.NOT_FOUND, e);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<List<ErrorResponse>> handleAccessDenied(AccessDeniedException e) {
		return buildErrorResponse(NO_ACCESS, "user.access.rights.message", e.getMessage(), HttpStatus.UNAUTHORIZED, null);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<List<ErrorResponse>> handleBadCredentials(BadCredentialsException e) {
		return buildErrorResponse(WRONG_PASSWORD, "user.wrong.password", e.getMessage(), HttpStatus.FORBIDDEN, null);
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<List<ErrorResponse>> handleDataAccess(DataAccessException e) {
		return buildErrorResponse(DB_ERROR, "data.access.exception.message", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e);
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity<List<ErrorResponse>> handleIO(IOException e) {
		return buildErrorResponse(SYSTEM_ERROR, "io.exception.message", e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, e);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<List<ErrorResponse>> handleUserAlreadyExists(UserAlreadyExistsException e) {
		return buildErrorResponse(USER_EXISTS, "username.already.registered", e.getMessage(), HttpStatus.CONFLICT, null);
	}

	@ExceptionHandler({EmailSendFailedException.class, MailSendException.class})
	public ResponseEntity<List<ErrorResponse>> handleEmailError(Exception e) {
		return buildErrorResponse(
				EMAIL_ERROR,
				"email.sending.failed",
				e.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				e
		);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<ErrorResponse>> handleValidationErrors(MethodArgumentNotValidException ex) {
		List<ErrorResponse> responses = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> new ErrorResponse(INVALID_PARAMS, messageConfig.getMessage(error.getDefaultMessage()), null))
				.toList();
		log.error(ERROR_RESPONSE_LOG, responses);
		return ResponseEntity.badRequest().body(responses);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<List<ErrorResponse>> handleGeneric(Exception e) {
		return buildErrorResponse(SYSTEM_ERROR, "general.exception", e.getMessage(), HttpStatus.FORBIDDEN, e);
	}
}