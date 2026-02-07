package com.jjsttk.goodswarehouse.service.order.dto.internal;

import lombok.Builder;

import java.util.Map;

@Builder
public record CustomerExternalData(
        Map<Long, CustomerExternalDataInfo> infoMap
) {
}
