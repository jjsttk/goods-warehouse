package com.jjsttk.goodswarehouse.shared.configuration.service.web.client.config;

import com.jjsttk.goodswarehouse.shared.configuration.property.rest.AccountServiceProperties;
import com.jjsttk.goodswarehouse.shared.configuration.property.rest.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.shared.configuration.property.rest.InnServiceProperties;
import com.jjsttk.goodswarehouse.shared.configuration.property.rest.RestServiceProperties;
import com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory.WebClientFactory;
import com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory.WebClientFactoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient instances used in the application.
 * Configures timeouts, retry strategies, and connection settings for HTTP clients.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final WebClientFactory webClientFactory;

    /**
     * Creates a configured WebClient instance for exchange service communication.
     *
     * <p>This bean provides a pre-configured WebClient specifically tailored for
     * communicating with external exchange rate service. The client includes:
     * <ul>
     *   <li>Timeout configurations (connect and read timeouts)</li>
     *   <li>Retry strategies for read timeouts and server errors</li>
     *   <li>Fixed backoff for retry mechanisms</li>
     *   <li>Proper connection pooling and HTTP client settings</li>
     * </ul>
     * </p>
     *
     * @param props the specific configuration properties for the exchange service
     * @return fully configured WebClient instance for exchange service API calls
     * @see WebClientFactoryImpl#create(RestServiceProperties)
     */
    @Bean
    public WebClient exchangeServiceWebClient(ExchangeServiceProperties props) {
        return webClientFactory.create(props);
    }

    /**
     * Creates a configured WebClient instance for Inn service communication.
     *
     * <p>This bean provides a pre-configured WebClient specifically tailored for
     * communicating with external Inn web service. The client includes:
     * <ul>
     *   <li>Timeout configurations (connect and read timeouts)</li>
     *   <li>Retry strategies for read timeouts and server errors</li>
     *   <li>Fixed backoff for retry mechanisms</li>
     *   <li>Proper connection pooling and HTTP client settings</li>
     * </ul>
     * </p>
     *
     * @param props the specific configuration properties for the Inn service
     * @return fully configured WebClient instance for Inn service API calls
     * @see WebClientFactoryImpl#create(RestServiceProperties)
     */
    @Bean
    public WebClient innWebClient(InnServiceProperties props) {
        return webClientFactory.create(props);
    }

    /**
     * Creates a configured WebClient instance for Account service communication.
     *
     * <p>This bean provides a pre-configured WebClient specifically tailored for
     * communicating with external Account web service. The client includes:
     * <ul>
     *   <li>Timeout configurations (connect and read timeouts)</li>
     *   <li>Retry strategies for read timeouts and server errors</li>
     *   <li>Fixed backoff for retry mechanisms</li>
     *   <li>Proper connection pooling and HTTP client settings</li>
     * </ul>
     * </p>
     *
     * @param props the specific configuration properties for the account service
     * @return fully configured WebClient instance for account service API calls
     * @see WebClientFactoryImpl#create(RestServiceProperties)
     */
    @Bean
    public WebClient accountServiceWebClient(AccountServiceProperties props) {
        return webClientFactory.create(props);
    }
}
