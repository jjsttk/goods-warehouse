package com.jjsttk.goodswarehouse.exception.handler;
import com.jjsttk.goodswarehouse.exception.ErrorResponse;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.FieldValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleFieldValidationExceptionsShouldReturnBadRequestWithErrors() {
        var validationErrors = Map.of(
                "article", List.of("Article must be unique"),
                "price", List.of("Price must be positive")
        );
        var ex = new FieldValidationException(validationErrors);

        ResponseEntity<Object> response = handler.handleFieldValidationExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.message()).isEqualTo("Validation failed for 2 field(s)");
        assertThat(errorResponse.exception()).isEqualTo("FieldValidationException");
        assertThat(errorResponse.validationErrors())
                .containsKeys("article", "price")
                .satisfies(map -> {
                    assertThat(map.get("article")).containsExactly("Article must be unique");
                    assertThat(map.get("price")).containsExactly("Price must be positive");
                });
    }

    @Test
    void handleResourceNotFoundExceptionShouldReturnNotFound() {
        var ex = new ResourceNotFoundException("Product not found");

        ResponseEntity<Object> response = handler.handleResourceNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.exception()).isEqualTo("ResourceNotFoundException");
        assertThat(errorResponse.message()).isEqualTo("Product not found");
    }

    @Test
    void handleDataIntegrityViolationExceptionShouldReturnUnprocessableEntity() {
        var ex = new DataIntegrityViolationException("Constraint violation");

        ResponseEntity<Object> response = handler.handleDataIntegrity(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.exception()).isEqualTo("DataIntegrityViolationException");
        assertThat(errorResponse.message()).isEqualTo("Constraint violation");
    }

    @Test
    void handleGenericExceptionShouldReturnInternalServerError() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<Object> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.exception()).isEqualTo("Exception");
        assertThat(errorResponse.message()).isEqualTo("Unexpected error");
    }

    @Test
    void handleMethodArgumentNotValidShouldReturnValidationErrors() {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "product");
        bindingResult.addError(new FieldError("product", "name", "must not be null"));
        bindingResult.addError(new FieldError("product", "price", "must be positive"));

        var ex = new MethodArgumentNotValidException(null, bindingResult);

        var response = handler.handleMethodArgumentNotValid(
                ex, null, HttpStatusCode.valueOf(400), null
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.message()).isEqualTo("Validation failed for 2 field(s)");
        assertThat(errorResponse.validationErrors())
                .containsKeys("name", "price")
                .satisfies(map -> {
                    assertThat(map.get("name")).containsExactly("must not be null");
                    assertThat(map.get("price")).containsExactly("must be positive");
                });
    }

    @Test
    void handleJakartaValidationExceptionShouldReturnBadRequest() {
        var ex = new jakarta.validation.ValidationException("Invalid input");

        ResponseEntity<Object> response = handler.handleJakartaValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.exception()).isEqualTo("ValidationException");
        assertThat(errorResponse.message()).isEqualTo("Invalid input");
    }

    @Test
    void handleHttpMessageNotReadableShouldReturnBadRequest() {
        HttpInputMessage httpInputMessage = mock(HttpInputMessage.class);
        var ex = new HttpMessageNotReadableException(
                "Invalid JSON",
                new RuntimeException("Parse error"),
                httpInputMessage
        );

        var response = handler.handleHttpMessageNotReadable(
                ex, null, HttpStatusCode.valueOf(400), null
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.message()).contains("Invalid JSON");
        assertThat(errorResponse.exception()).isEqualTo("HttpMessageNotReadableException");
    }
}
