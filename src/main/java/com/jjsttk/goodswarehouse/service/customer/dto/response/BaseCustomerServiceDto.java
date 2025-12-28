package com.jjsttk.goodswarehouse.service.customer.dto.response;

import lombok.Builder;

@Builder
public record BaseCustomerServiceDto(
        Long id,
        String login,
        String email,
        Boolean isActive
) {
}
