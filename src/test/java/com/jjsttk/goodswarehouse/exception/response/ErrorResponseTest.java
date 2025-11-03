package com.jjsttk.goodswarehouse.exception.response;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorResponseTest {

    @Test
    void builderShouldCreateErrorResponse() {
        var now = OffsetDateTime.now();

        ErrorResponse response = ErrorResponse.builder()
                .message("Validation failed")
                .exception("TestException")
                .source("TestSource")
                .dateTime(now)
                .build();

        assertThat(response.message()).isEqualTo("Validation failed");
        assertThat(response.exception()).isEqualTo("TestException");
        assertThat(response.source()).isEqualTo("TestSource");
        assertThat(response.dateTime()).isEqualTo(now);
    }
}
