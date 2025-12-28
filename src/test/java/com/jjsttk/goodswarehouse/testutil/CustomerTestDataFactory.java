package com.jjsttk.goodswarehouse.testutil;

import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerServiceDto;
import org.instancio.Instancio;
import static org.instancio.Select.field;

public class CustomerTestDataFactory {

    public static CustomerEntity getCustomerEntityWithGeneratedId(boolean isActive) {
        return Instancio.of(CustomerEntity.class)
                .set(field("isActive"), isActive)
                .create();
    }

    public static CustomerEntity getCustomerEntityWithoutGeneratedId(boolean isActive) {
        return Instancio.of(CustomerEntity.class)
                .set(field("isActive"), isActive)
                .create();
    }


    public static BaseCustomerServiceDto getCustomerServiceDtoFromCustomerEntity(CustomerEntity customerEntity) {
        return Instancio.of(BaseCustomerServiceDto.class)
                .set(field("id"), customerEntity.getId())
                .set(field("email"), customerEntity.getEmail())
                .set(field("login"), customerEntity.getLogin())
                .set(field("isActive"), customerEntity.getIsActive())
                .create();
    }
}
