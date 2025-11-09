package com.jjsttk.goodswarehouse.service.product.search.advanced.param;

import com.jjsttk.goodswarehouse.shared.enums.FilterOperation;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BigDecimalParam(
        String field,
        BigDecimal value,
        FilterOperation operation
) implements AdvancedSearchParam<BigDecimal> {
}
