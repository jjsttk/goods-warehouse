package com.jjsttk.goodswarehouse.service.product.search.advanced.param;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.jjsttk.goodswarehouse.enums.FilterOperation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "field",
        visible = true
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = StringParam.class, names = {"name", "description"}),
        @JsonSubTypes.Type(value = BigDecimalParam.class, names = {"price", "quantity"}),
        @JsonSubTypes.Type(value = LocalDateParam.class, name = "createdAt")
})
public sealed interface AdvancedSearchParam<T> permits StringParam, BigDecimalParam, LocalDateParam {

    @NotBlank(message = "Field cannot be blank")
    String field();

    @NotNull(message = "Value cannot be null")
    T value();

    @NotNull(message = "Operation cannot be null")
    FilterOperation operation();
}
