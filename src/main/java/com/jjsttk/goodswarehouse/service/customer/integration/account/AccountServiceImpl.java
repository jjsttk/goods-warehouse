package com.jjsttk.goodswarehouse.service.customer.integration.account;

import com.jjsttk.goodswarehouse.exception.service.customer.account.AccountServiceException;
import com.jjsttk.goodswarehouse.shared.configuration.property.rest.AccountServiceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final WebClient accountServiceWebClient;
    private final AccountServiceProperties properties;

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Map<String, String>> getAccountNumbersMapAsync(Collection<String> customerLogins) {
        return accountServiceWebClient.post()
                .uri(properties.getEndpoints().getAccNumbers())
                .bodyValue(customerLogins)
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<Map<String, String>>() {
                        }
                )
                .onErrorMap(ex -> new AccountServiceException(ex.getMessage(), ex))
                .toFuture();
    }
}
