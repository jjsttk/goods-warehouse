package com.jjsttk.goodswarehouse.service.search.advanced.param;

import com.jjsttk.goodswarehouse.enums.FilterOperation;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BigDecimalParam(
        String field,
        BigDecimal value,
        FilterOperation operation
) implements AdvancedSearchParam<BigDecimal> {
}
