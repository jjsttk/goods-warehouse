package com.jjsttk.goodswarehouse.service.order.product.dto.response;

import lombok.Builder;

import java.util.Collection;

@Builder
public record OrderProductServiceResponseContainer<T>(
        Collection<T> orderProducts
) {
}
