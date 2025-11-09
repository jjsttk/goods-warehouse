package com.jjsttk.goodswarehouse.service.product.search.advanced.param;

import com.jjsttk.goodswarehouse.shared.enums.FilterOperation;
import lombok.Builder;

@Builder
public record StringParam(String field, String value,
                          FilterOperation operation) implements AdvancedSearchParam<String> {
}
