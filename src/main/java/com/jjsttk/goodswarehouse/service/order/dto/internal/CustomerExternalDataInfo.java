package com.jjsttk.goodswarehouse.service.order.dto.internal;

import lombok.Builder;

@Builder
public record CustomerExternalDataInfo(
        String inn,
        String accNum
) {
}
