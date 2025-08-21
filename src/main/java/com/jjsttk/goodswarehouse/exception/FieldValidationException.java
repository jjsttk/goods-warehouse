package com.jjsttk.goodswarehouse.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class FieldValidationException extends RuntimeException {
    private final Map<String, List<String>> validationErrors;

    public FieldValidationException(Map<String, List<String>> validationErrorsInput) {
        super("Validation failed for %d field(s)".formatted(validationErrorsInput.size()));
        this.validationErrors = validationErrorsInput;
    }
}
