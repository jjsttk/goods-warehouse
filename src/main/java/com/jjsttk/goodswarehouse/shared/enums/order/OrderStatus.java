package com.jjsttk.goodswarehouse.shared.enums.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

@Schema(description = "OrderEntity status")
public enum OrderStatus {
    @Schema(description = "Created")
    CREATED,

    @Schema(description = "Confirmed")
    CONFIRMED,

    @Schema(description = "Cancelled")
    CANCELLED,

    @Schema(description = "Done")
    DONE,

    @Schema(description = "Rejected")
    REJECTED;

    @JsonCreator
    public static @Nullable OrderStatus forValue(@Nullable String value) {
        return value == null ? null : OrderStatus.valueOf(value.strip().toUpperCase());
    }
}
