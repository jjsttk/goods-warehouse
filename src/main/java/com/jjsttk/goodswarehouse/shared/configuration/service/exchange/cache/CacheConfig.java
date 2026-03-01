package com.jjsttk.goodswarehouse.shared.configuration.service.exchange.cache;

import com.jjsttk.goodswarehouse.shared.configuration.property.cache.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class CacheConfig {

    /**
     * Configures a {@link RedisCacheManager} with strictly defined caches.
     * <p>
     * Validates that each {@link CacheProperties} has a unique, non-null cache name.
     * Dynamic cache creation is disabled to ensure all used caches are explicitly configured.
     * </p>
     *
     * @param connectionFactory the factory to establish Redis connections
     * @param allProps          list of all available cache property implementations
     * @return configured RedisCacheManager
     * @throws NullPointerException  if a cache name is missing
     * @throws IllegalStateException if duplicate cache names are detected
     */
    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            List<CacheProperties> allProps
    ) {

        var cacheConfigs = allProps.stream()
                .collect(Collectors.toMap(
                        // С Supplier — строка формируется только при обнаружении null
                        prop -> Objects.requireNonNull(
                                prop.getCacheName(),
                                () -> String.format(
                                        "Implementation %s has null cache name!",
                                        prop.getClass().getSimpleName()
                                )
                        ),
                        this::createConfiguration,
                        (existing, replacement) -> {
                            throw new IllegalStateException("Duplicate cache name found in configurations");
                        }
                ));

        return RedisCacheManager.builder(connectionFactory)
                .disableCreateOnMissingCache()
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    private RedisCacheConfiguration createConfiguration(CacheProperties cacheProperties) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(cacheProperties.getExpireAfterWrite())
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );
    }
}
