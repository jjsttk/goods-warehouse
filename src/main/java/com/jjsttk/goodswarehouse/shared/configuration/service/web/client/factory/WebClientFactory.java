package com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory;

import com.jjsttk.goodswarehouse.shared.configuration.property.rest.RestServiceProperties;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Factory for creating and configuring {@link WebClient} instances.
 * <p>
 * Provides a unified mechanism to build HTTP clients based on shared
 * service configuration properties such as timeouts, retries, and base URLs.
 */
public interface WebClientFactory {

    /**
     * Creates and configures a new {@link WebClient} instance based on the provided properties.
     *
     * @param properties the specific service configuration including host,
     *                   timeout settings, and retry policies
     * @return a fully configured {@link WebClient} instance ready for service communication
     */
    WebClient create(RestServiceProperties properties);
}
