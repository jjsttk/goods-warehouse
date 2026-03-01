package com.jjsttk.goodswarehouse.shared.configuration.property.cache;

import java.time.Duration;

public interface CacheProperties {
    String getCacheName();
    Duration getExpireAfterWrite();
}
