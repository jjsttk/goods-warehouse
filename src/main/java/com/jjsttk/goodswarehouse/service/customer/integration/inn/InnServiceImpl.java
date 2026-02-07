package com.jjsttk.goodswarehouse.service.customer.integration.inn;

import com.jjsttk.goodswarehouse.exception.service.customer.inn.InnServiceException;
import com.jjsttk.goodswarehouse.shared.configuration.property.rest.InnServiceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class InnServiceImpl implements InnService {
    private final WebClient innWebClient;
    private final InnServiceProperties properties;

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Map<String, String>> getLoginToInnMapAsync(Collection<String> customerLogins) {

        return innWebClient.post()
                .uri(properties.getEndpoints().getInns())
                .bodyValue(customerLogins)
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<Map<String, String>>() {
                        }
                )
                .onErrorMap(ex -> new InnServiceException(ex.getMessage(), ex))
                .toFuture();
    }
}
