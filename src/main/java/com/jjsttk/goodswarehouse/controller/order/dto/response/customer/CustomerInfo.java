package com.jjsttk.goodswarehouse.controller.order.dto.response.customer;

import lombok.Builder;

@Builder
public record CustomerInfo(
        Long id,
        String accountNumber,
        String email,
        String inn
) {
}
