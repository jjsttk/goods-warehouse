package com.jjsttk.goodswarehouse.mapper.customer;

import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerServiceDto;

public interface CustomerConverter {

    BaseCustomerServiceDto mapToServiceResponse(
            CustomerEntity entity
    );
}
