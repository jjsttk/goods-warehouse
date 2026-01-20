package com.jjsttk.goodswarehouse.service.order.product.dto.response;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.util.Collection;

@Builder
public record OrderProductResponseContainer<T>(
        @NonNull Collection<T> orderProducts
) {
}
