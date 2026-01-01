package com.devops.api.redis.exception;

import java.io.IOException;
import java.time.Instant;
import java.util.NoSuchElementException;

import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.lettuce.core.RedisCommandExecutionException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), Instant.now()));
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), Instant.now()));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now()));
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity<ErrorResponse> handleCertificateIOException(IOException ex) {
		log.error("Failed to load Redis CA certificate", ex);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now()));
	}

	@ExceptionHandler(RedisConnectionFailureException.class)
	public ResponseEntity<ErrorResponse> handleRedisConnectionFailure(RedisConnectionFailureException ex) {
		log.error("Redis connection failure", ex);

		Throwable cause = ex;
		while (cause != null) {
			if (cause instanceof RedisCommandExecutionException && cause.getMessage() != null
					&& cause.getMessage().contains("WRONGPASS")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(new ErrorResponse("Redis authentication failed: invalid username or password",
								HttpStatus.UNAUTHORIZED.value(), Instant.now()));
			}
			cause = cause.getCause();
		}

		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ErrorResponse("Cannot connect to Redis. Please check connection.",
						HttpStatus.SERVICE_UNAVAILABLE.value(), Instant.now()));
	}

	@ExceptionHandler(QueryTimeoutException.class)
	public ResponseEntity<ErrorResponse> redisConnectionTimeoutHandleIOException(QueryTimeoutException ex) {
		log.error("Failed to connect to Redis", ex);
		return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
				.body(new ErrorResponse(ex.getMessage(), HttpStatus.GATEWAY_TIMEOUT.value(), Instant.now()));
	}

}
