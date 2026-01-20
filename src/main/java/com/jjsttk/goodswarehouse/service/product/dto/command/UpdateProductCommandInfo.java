package com.jjsttk.goodswarehouse.service.product.dto.command;

import com.jjsttk.goodswarehouse.shared.enums.product.Category;
import lombok.Builder;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@Builder
public record UpdateProductCommandInfo(
        @Nullable String name,
        @Nullable String article,
        @Nullable String description,
        @Nullable Category category,
        @Nullable BigDecimal price,
        @Nullable BigDecimal quantity,
        @Nullable Boolean isAvailable
) {
}
