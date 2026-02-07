package com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * Factory for creating and configuring {@link WebClient} instances.
 */
public interface WebClientFactory {

    /**
     * Creates and configures a new {@link WebClient} instance based on service-specific properties.
     *
     * @return a fully configured {@link WebClient} with timeouts, retries, and base URL.
     */
    WebClient create();
}
