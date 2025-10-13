package com.jjsttk.goodswarehouse.service.search.advanced.param;

import com.jjsttk.goodswarehouse.enums.FilterOperation;
import lombok.Builder;

@Builder
public record StringParam(String field, String value,
                          FilterOperation operation) implements AdvancedSearchParam<String> {
}
