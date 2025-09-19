
package com.jjsttk.goodswarehouse.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
@Schema(description = "Error response payload")
public record ErrorResponse(
        @Schema(
                description = "Error message for the client",
                example = "Validation failed"
        )
        String message,

        @Schema(
                description = "Exception class name",
                example = "MethodArgumentNotValidException"
        )
        String exception,

        @Schema(
                description = "Source of the error (controller, service, etc.)",
                example = "ProductController"
        )
        String source,

        @Schema(
                description = "Timestamp of the error event",
                example = "2025-08-25T14:33:45+03:00"
        )
        OffsetDateTime dateTime
) {
}
