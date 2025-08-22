package com.jjsttk.goodswarehouse.exception.handler;

import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GlobalExceptionHandlerParameterizedTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    static Stream<TestData> exceptionProvider() {
        return Stream.of(
                new TestData(new ValidationException("Invalid data"),
                        HttpStatus.BAD_REQUEST,
                        "Validation Error",
                        "Invalid data"),
                new TestData(new ResourceNotFoundException("Product not found"),
                        HttpStatus.NOT_FOUND,
                        "Resource Not Found",
                        "Product not found"),
                new TestData(new DataIntegrityViolationException("Constraint violation"),
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Data Integrity Violation",
                        "Constraint violation"),
                new TestData(new Exception("Something went wrong"),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal Server Error",
                        "Something went wrong")
        );
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void handleExceptionsShouldReturnExpectedResponse(TestData data) {
        ResponseEntity<Map<String, Object>> response = switch (data.exception) {
            case ValidationException validationException ->
                    handler.handleValidationException(validationException);
            case ResourceNotFoundException resourceNotFoundException ->
                    handler.handleResourceNotFoundException(resourceNotFoundException);
            case DataIntegrityViolationException dataIntegrityViolationException ->
                    handler.handleDataIntegrityViolationException(dataIntegrityViolationException);
            default -> handler.handleGenericException(data.exception);
        };

        Map<String, Object> body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(data.status);
        assertThat(body).hasFieldOrPropertyWithValue("status", data.status.value())
                .hasFieldOrPropertyWithValue("error", data.error)
                .hasFieldOrPropertyWithValue("message", data.message);
        assertThat(body.get("timestamp")).isNotNull();
    }

    record TestData(
            Exception exception,
            HttpStatus status,
            String error,
            String message
    ) {
    }
}
