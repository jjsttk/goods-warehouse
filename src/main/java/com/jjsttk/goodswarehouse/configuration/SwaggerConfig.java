package com.jjsttk.goodswarehouse.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger / OpenAPI documentation.
 * Defines API groups and paths for versioned APIs.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Defines the OpenAPI group for version 1 of the product API.
     *
     * @return a configured {@link GroupedOpenApi} instance for API v1
     */
    @Bean
    public GroupedOpenApi productApiV1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
