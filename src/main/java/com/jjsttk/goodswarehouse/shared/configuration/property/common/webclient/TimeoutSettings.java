package com.jjsttk.goodswarehouse.shared.configuration.property.common.webclient;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class TimeoutSettings {
    private Duration connect = Duration.ofSeconds(5);
    private Duration read = Duration.ofSeconds(5);
}
