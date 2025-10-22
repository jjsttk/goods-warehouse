package com.jjsttk.goodswarehouse.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;


/**
 * Configuration class for cache setup using Caffeine cache provider.
 * This configuration defines the cache behavior and management for the application.
 *
 * <p>The configuration sets up a cache with a time-based expiration policy
 * where entries expire 1 minute after being written to the cache.</p>
 *
 * <p>Example usage:
 * <pre>
 * {@literal @}Cacheable("cacheName")
 * public Object expensiveMethod(String key) {
 *     // This result will be cached for 1 minute
 * }
 * </pre>
 * </p>
 */
@Configuration
public class CacheConfig {

    /**
     * Creates and configures a Caffeine cache instance with specific settings.
     *
     * <p>This bean defines the cache configuration with the following properties:
     * <ul>
     *   <li>Entries expire 1 minute after being written to the cache</li>
     * </ul>
     * </p>
     *
     * @return Configured Caffeine cache instance with expiration policy
     * @see Caffeine
     */
    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES);
    }

    /**
     * Creates a CacheManager that uses Caffeine as the caching provider.
     *
     * <p>This bean integrates the Caffeine cache configuration with Spring's
     * cache abstraction. The CacheManager is responsible for creating and
     * managing cache instances throughout the application.</p>
     *
     * @param caffeine The pre-configured Caffeine instance defining cache behavior
     * @return CacheManager instance configured with Caffeine cache provider
     * @see CacheManager
     * @see CaffeineCacheManager
     */
    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        var caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
