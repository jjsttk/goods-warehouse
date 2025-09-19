package com.jjsttk.goodswarehouse.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger / OpenAPI documentation.
 * Defines API groups and paths for versioned APIs.
 */

@OpenAPIDefinition(
        info = @Info(
                title = "Goods Warehouse API",
                version = "1.0",
                description = "REST API для управления складом товаров"),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local server")
        }
)
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
