
package com.jjsttk.goodswarehouse.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Builder
@Schema(description = "Error response payload")
public record ErrorResponse(
        @Schema(description = "Error message for the client", example = "Validation failed")
        String message,

        @Schema(description = "Detailed validation errors")
        Map<String, List<String>> validationErrors,

        @Schema(description = "Exception class name", example = "MethodArgumentNotValidException")
        String exception,

        @Schema(description = "Source of the error (controller, service, etc.)", example = "ProductController")
        String source,

        @Schema(description = "Timestamp of the error event", example = "2025-08-25T14:33:45+03:00")
        OffsetDateTime dateTime
) {
    public ErrorResponse {
        if (validationErrors == null) {
            validationErrors = Map.of();
        }
    }
}
