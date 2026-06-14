package com.inventario.inventory_backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
		return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	ResponseEntity<ApiError> handleDuplicateResource(DuplicateResourceException exception, HttpServletRequest request) {
		return buildError(HttpStatus.CONFLICT, exception.getMessage(), request, null);
	}

	@ExceptionHandler(BadCredentialsException.class)
	ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException exception, HttpServletRequest request) {
		return buildError(HttpStatus.UNAUTHORIZED, exception.getMessage(), request, null);
	}

	@ExceptionHandler(IllegalStateException.class)
	ResponseEntity<ApiError> handleIllegalState(IllegalStateException exception, HttpServletRequest request) {
		return buildError(HttpStatus.CONFLICT, exception.getMessage(), request, null);
	}

	@ExceptionHandler({AccessDeniedException.class, SecurityException.class})
	ResponseEntity<ApiError> handleAccessDenied(RuntimeException exception, HttpServletRequest request) {
		return buildError(HttpStatus.FORBIDDEN, exception.getMessage(), request, null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
		Map<String, String> validationErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));

		return buildError(HttpStatus.BAD_REQUEST, "La solicitud contiene datos invalidos", request, validationErrors);
	}

	private ResponseEntity<ApiError> buildError(
			HttpStatus status,
			String message,
			HttpServletRequest request,
			Map<String, String> validationErrors
	) {
		ApiError apiError = new ApiError(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI(),
				validationErrors
		);

		return ResponseEntity.status(status).body(apiError);
	}
}
