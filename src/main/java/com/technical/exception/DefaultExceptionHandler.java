package com.technical.exception;

import com.technical.commonutil.MasterCodeBase;
import com.technical.dto.common.ErrorResponse;
import com.technical.dto.common.ExceptionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
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
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class DefaultExceptionHandler extends MasterCodeBase {

	private static final String SYSTEM_ERROR = "SYSTEM_ERROR";
	private static final String DB_ERROR = "DB_ERROR";
	private static final String USER_NOT_FOUND = "USER_NOT_FOUND";
	private static final String NO_ACCESS = "NO_ACCESS";
	private static final String WRONG_PASSWORD = "WRONG_PASSWORD";
	private static final String USER_EXISTS = "USER_EXISTS";
	private static final String INVALID_PARAMS = "INVALID_PARAMS";
	private static final String EMAIL_ERROR = "EMAIL_ERROR";
	private static final String EMAIL_REQUIRE_VERIFICATION = "EMAIL_REQUIRE_VERIFICATION";

	private static final Map<Class<? extends Exception>, ExceptionConfig> EXCEPTION_CONFIGS = Map.ofEntries(
		Map.entry(DataIntegrityViolationException.class, new ExceptionConfig(DB_ERROR, "database.error", HttpStatus.CONFLICT)),
		Map.entry(ParseException.class, new ExceptionConfig(SYSTEM_ERROR, "date.parsing.error", HttpStatus.FORBIDDEN)),
		Map.entry(NullPointerException.class, new ExceptionConfig(SYSTEM_ERROR, "system.error", HttpStatus.NO_CONTENT)),
		Map.entry(DataAccessException.class, new ExceptionConfig(DB_ERROR, "data.access.exception.message", HttpStatus.INTERNAL_SERVER_ERROR)),
		Map.entry(IOException.class, new ExceptionConfig(SYSTEM_ERROR, "io.exception.message", HttpStatus.UNSUPPORTED_MEDIA_TYPE)),
		Map.entry(EmailSendFailedException.class, new ExceptionConfig(EMAIL_ERROR, "email.sending.failed", HttpStatus.INTERNAL_SERVER_ERROR)),
		Map.entry(MailSendException.class, new ExceptionConfig(EMAIL_ERROR, "email.sending.failed", HttpStatus.INTERNAL_SERVER_ERROR)),

		Map.entry(EmailRequireVerificationException.class, new ExceptionConfig(EMAIL_REQUIRE_VERIFICATION, "email.require.verification", HttpStatus.UNAUTHORIZED, false)),
		Map.entry(ResourceNotFoundException.class, new ExceptionConfig(USER_NOT_FOUND, "user.not.found", HttpStatus.NOT_FOUND, false)),
		Map.entry(UsernameNotFoundException.class, new ExceptionConfig(USER_NOT_FOUND, "username.not.found", HttpStatus.NOT_FOUND, false)),
		Map.entry(AccessDeniedException.class, new ExceptionConfig(NO_ACCESS, "user.access.rights.message", HttpStatus.UNAUTHORIZED, false)),
		Map.entry(BadCredentialsException.class, new ExceptionConfig(WRONG_PASSWORD, "user.wrong.password", HttpStatus.FORBIDDEN, false)),
		Map.entry(UserAlreadyExistsException.class, new ExceptionConfig(USER_EXISTS, "username.already.registered", HttpStatus.CONFLICT, false)),

		// This should be last as it will catch all exceptions
		Map.entry(RuntimeException.class, new ExceptionConfig(SYSTEM_ERROR, "system.error", HttpStatus.EXPECTATION_FAILED))
	);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<ErrorResponse>> handleValidationErrors(MethodArgumentNotValidException ex) {
		final var responses = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> new ErrorResponse(INVALID_PARAMS, messageConfig.getMessage(error.getDefaultMessage()), null))
				.toList();
		log.info("Validation Failed Response: {}", responses);
		return ResponseEntity.badRequest().body(responses);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<List<ErrorResponse>> handleException(Exception e) {
		// Handle mapped exceptions
		for (Map.Entry<Class<? extends Exception>, ExceptionConfig> entry : EXCEPTION_CONFIGS.entrySet()) {
			if (entry.getKey().isInstance(e)) {
				ExceptionConfig config = entry.getValue();
				return createErrorResponse(config, e);
			}
		}

		// Default fallback for unmapped exceptions
		return createErrorResponse(new ExceptionConfig(SYSTEM_ERROR, "general.exception", HttpStatus.FORBIDDEN), e);
	}
}
