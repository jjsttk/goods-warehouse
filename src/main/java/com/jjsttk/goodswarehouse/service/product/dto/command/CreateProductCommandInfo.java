package com.jjsttk.goodswarehouse.service.product.dto.command;

import com.jjsttk.goodswarehouse.shared.enums.product.Category;
import lombok.Builder;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@Builder
public record CreateProductCommandInfo(
        String name,
        String article,
        @Nullable String description,
        Category category,
        BigDecimal price,
        BigDecimal quantity,
        Boolean isAvailable
) {
}
