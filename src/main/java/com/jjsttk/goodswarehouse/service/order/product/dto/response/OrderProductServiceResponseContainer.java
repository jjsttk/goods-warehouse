package com.jjsttk.goodswarehouse.service.order.product.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderProductServiceResponseContainer<T>(
        List<T> orderProducts
) {
}
