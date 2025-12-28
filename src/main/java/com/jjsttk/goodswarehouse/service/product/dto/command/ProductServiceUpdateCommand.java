package com.jjsttk.goodswarehouse.service.product.dto.command;

import com.jjsttk.goodswarehouse.shared.enums.product.Category;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductServiceUpdateCommand(
        String name,
        String article,
        String description,
        Category category,
        BigDecimal price,
        BigDecimal quantity,
        Boolean isAvailable
) {
}
