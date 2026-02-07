package com.jjsttk.goodswarehouse.shared.configuration.property.common.webclient;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class RetryPolicy {
    private int maxAttempts = 3;

    private Duration backoff = Duration.ofSeconds(1);
}
