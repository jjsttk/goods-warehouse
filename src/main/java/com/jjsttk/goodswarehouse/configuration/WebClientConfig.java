package com.jjsttk.goodswarehouse.configuration;

import com.jjsttk.goodswarehouse.configuration.property.service.exchange.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.configuration.service.exchange.ExchangeServiceWebClientStrategy;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * Configuration class for WebClient instances used in the application.
 * Configures timeouts, retry strategies, and connection settings for HTTP clients.
 */
@Slf4j
@Configuration
public class WebClientConfig {

    /**
     * Creates a configured WebClient instance for exchange rate service communication.
     * Applies timeout settings, retry strategies for read timeouts and server errors,
     * and configures the underlying HTTP client.
     *
     * @param properties                the exchange service configuration properties
     * @param exchangeServiceWebClientStrategy contains pre-configured retry strategies
     * @return configured WebClient instance for exchange rate API calls
     */
    @Bean
    public WebClient exchangeServiceWebClient(
            ExchangeServiceProperties properties,
            ExchangeServiceWebClientStrategy exchangeServiceWebClientStrategy
    ) {
        var timeoutSettings = properties.getTimeout();

        // Read timeout
        var readTimeoutDuration = timeoutSettings.getRead();
        var readTimeoutRetryMaxAttempts = timeoutSettings.getRetry()
                .getReadTimeout().getMaxAttempts();
        var readTimeoutRetryBackoffDuration = timeoutSettings.getRetry()
                .getReadTimeout().getBackoff();

        // Internal error
        var serverErrorRetryMaxAttempts = timeoutSettings.getRetry()
                .getServerErrors().getMaxAttempts();
        var serverErrorRetryBackoffDuration = timeoutSettings.getRetry()
                .getServerErrors().getBackoff();

        // Connect timeout
        var connectTimeoutDuration = timeoutSettings.getConnect();

        var readTimeoutRetryFilter = exchangeServiceWebClientStrategy.getReadTimeoutRetryFilterFunction(
                readTimeoutRetryMaxAttempts, readTimeoutRetryBackoffDuration
        );
        var serverFailureRetryFilter = exchangeServiceWebClientStrategy.getServerErrorRetryFilterFunction(
                serverErrorRetryMaxAttempts, serverErrorRetryBackoffDuration
        );

        log.info("Applying timeout settings - Connect: {}, Read: {}",
                connectTimeoutDuration, readTimeoutDuration);
        log.info("Applying retry settings - Read timeout: {} attempts (backoff: {}), "
                        + "Server errors: {} attempt (backoff: {})",
                readTimeoutRetryMaxAttempts, readTimeoutRetryBackoffDuration,
                serverErrorRetryMaxAttempts, serverErrorRetryBackoffDuration);

        var httpClient = HttpClient.create()
                .responseTimeout(readTimeoutDuration)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeoutDuration.toMillis());


        return WebClient.builder()
                .baseUrl(properties.getHost())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(readTimeoutRetryFilter)
                .filter(serverFailureRetryFilter)
                .build();
    }
}
