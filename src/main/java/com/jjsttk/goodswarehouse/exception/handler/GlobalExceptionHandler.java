package com.jjsttk.goodswarehouse.exception.handler;

import com.jjsttk.goodswarehouse.exception.ErrorResponse;
import com.jjsttk.goodswarehouse.exception.NotUniqueArticleException;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public final class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        var validationMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildResponse(HttpStatus.BAD_REQUEST, validationMessage, ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(NotUniqueArticleException.class)
    public ResponseEntity<ErrorResponse> handleNotUniqueArticle(
            NotUniqueArticleException ex
    ) {
        return buildResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleJakartaValidation(ValidationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    // ========== PRIVATE HELPERS ==========

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, Exception ex) {
        return buildResponse(status, ex.getMessage(), ex);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String customMessage, Exception ex) {
        log.error("Handled exception: {}", ex.getClass().getSimpleName(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(customMessage)
                .exception(ex.getClass().getSimpleName())
                .source(ex.getStackTrace()[0].getClassName())
                .dateTime(OffsetDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}
