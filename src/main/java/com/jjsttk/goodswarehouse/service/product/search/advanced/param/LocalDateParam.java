package com.jjsttk.goodswarehouse.service.product.search.advanced.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jjsttk.goodswarehouse.shared.enums.FilterOperation;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LocalDateParam(
        String field,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate value,

        FilterOperation operation
) implements AdvancedSearchParam<LocalDate> {
}
