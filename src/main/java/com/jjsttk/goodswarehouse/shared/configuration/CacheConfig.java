package com.jjsttk.goodswarehouse.shared.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.jjsttk.goodswarehouse.shared.configuration.property.cache.CacheProperties;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for cache beans in the application.
 * <p>
 * Configures Caffeine cache instances with properties-driven settings
 * for expiration, statistics recording, and cache naming.
 * </p>
 *
 * @see CacheProperties
 * @see CaffeineCache
 */
@Configuration
public class CacheConfig {

    /**
     * Creates a CaffeineCache instance for exchange rate client caching.
     * <p>
     * Configures the cache with write-based expiration and optional statistics
     * recording based on application properties. The cache name and expiration
     * settings are retrieved from {@link CacheProperties}.
     * </p>
     *
     * @param cacheProperties the cache configuration properties
     * @return configured CaffeineCache instance for exchange rate data
     * @see CacheProperties.ExchangeRateClientProperties#getCacheName()
     * @see CacheProperties.ExchangeRateClientProperties#getExpireAfterWrite()
     * @see CacheProperties.ExchangeRateClientProperties#isRecordStats()
     */
    @Bean
    public CaffeineCache buildCaffeineCache(CacheProperties cacheProperties) {
        var caffeinePrebuild = Caffeine.newBuilder()
                .expireAfterWrite(cacheProperties.getExchangeRateClient().getExpireAfterWrite());

        if (cacheProperties.getExchangeRateClient().isRecordStats()) {
            return new CaffeineCache(
                    cacheProperties.getExchangeRateClient().getCacheName(),
                    caffeinePrebuild.recordStats().build()
            );
        }

        return new CaffeineCache(
                cacheProperties.getExchangeRateClient().getCacheName(),
                caffeinePrebuild.build()
        );
    }
}
