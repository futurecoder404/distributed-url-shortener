package com.sarvesh.distributedurlshortener.exception;

import com.sarvesh.distributedurlshortener.common.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShortUrlNotFoundException.class)
    public ResponseEntity<ApiErrorResponse>
    handleShortUrlNotFoundException(
            ShortUrlNotFoundException ex
    ) {

        ApiErrorResponse errorResponse =
                ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse>
    handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        String errorMessage = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        ApiErrorResponse errorResponse =
                ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(errorMessage)
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(
            RateLimitExceededException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleRateLimitException(
            RateLimitExceededException exception
    ) {

        ApiErrorResponse errorResponse =
                ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.TOO_MANY_REQUESTS.value())
                        .message(exception.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(errorResponse);
    }

    @ExceptionHandler(
            InvalidCredentialsException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleInvalidCredentialsException(
            InvalidCredentialsException exception
    ) {

        ApiErrorResponse errorResponse =
                ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message(exception.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(
            ShortUrlExpiredException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleShortUrlExpiredException(
            ShortUrlExpiredException ex
    ) {

        ApiErrorResponse errorResponse =
                ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.GONE.value())
                        .message(ex.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(errorResponse);
    }

    @ExceptionHandler(
            CustomAliasAlreadyExistsException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleCustomAliasAlreadyExistsException(
            CustomAliasAlreadyExistsException ex
    ) {

        ApiErrorResponse errorResponse =
                ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CONFLICT.value())
                        .message(ex.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(
            ShortUrlInactiveException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleShortUrlInactiveException(
            ShortUrlInactiveException ex
    ) {

        ApiErrorResponse errorResponse =
                ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.GONE.value())
                        .message(ex.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(errorResponse);
    }

    @ExceptionHandler(
            UnauthorizedUrlAccessException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleUnauthorizedUrlAccessException(
            UnauthorizedUrlAccessException ex
    ) {

        ApiErrorResponse errorResponse =
                ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(403)
                        .message(ex.getMessage())
                        .build();

        return ResponseEntity
                .status(403)
                .body(errorResponse);
    }
}