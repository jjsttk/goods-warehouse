package com.jjsttk.goodswarehouse.exception.handler;

import com.jjsttk.goodswarehouse.exception.ErrorResponse;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.FieldValidationException;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public final class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Bean Validation (@Valid)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, List<String>> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        return buildValidationResponse(validationErrors, ex, status);
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<Object> handleFieldValidationExceptions(FieldValidationException ex) {
        return buildValidationResponse(ex.getValidationErrors(), ex, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleJakartaValidation(ValidationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    private ResponseEntity<Object> buildValidationResponse(Map<String, List<String>> validationErrors,
                                                                  Exception ex,
                                                                  HttpStatusCode status) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Validation failed for " + validationErrors.size() + " field(s)")
                .validationErrors(validationErrors)
                .exception(ex.getClass().getSimpleName())
                .source(ex.getStackTrace()[0].getClassName())
                .dateTime(OffsetDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status,
                                                        Exception ex,
                                                        Map<String, List<String>> validationErrors) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName())
                .validationErrors(validationErrors)
                .exception(ex.getClass().getSimpleName())
                .source(ex.getStackTrace()[0].getClassName())
                .dateTime(OffsetDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, Exception ex) {
        return buildResponse(status, ex, Map.of());
    }
}
