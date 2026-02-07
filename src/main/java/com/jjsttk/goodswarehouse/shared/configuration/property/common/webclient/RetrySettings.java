package com.jjsttk.goodswarehouse.shared.configuration.property.common.webclient;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
public class RetrySettings {
    @NestedConfigurationProperty
    private RetryPolicy readTimeout = new RetryPolicy();

    @NestedConfigurationProperty
    private RetryPolicy serverErrors = new RetryPolicy();
}
