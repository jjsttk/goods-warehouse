package com.jjsttk.goodswarehouse.shared.configuration.property.rest;

import com.jjsttk.goodswarehouse.shared.configuration.property.common.webclient.RetrySettings;
import com.jjsttk.goodswarehouse.shared.configuration.property.common.webclient.TimeoutSettings;

public interface RestServiceProperties {
    String getHost();
    TimeoutSettings getTimeout();
    RetrySettings getRetry();
}
