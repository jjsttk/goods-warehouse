package com.jjsttk.goodswarehouse.service.customer.integration.inn;

import com.jjsttk.goodswarehouse.exception.service.customer.inn.InnServiceException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface InnService {

    /**
     * Asynchronously retrieves a mapping of customer logins to their INNs.
     *
     * @param customerLogins collection of customer logins to look up.
     * @return a {@link CompletableFuture} containing a map where the key is the login
     * and the value is the corresponding INN.
     * @throws InnServiceException if an error occurs during the retrieval process.
     */
    CompletableFuture<Map<String, String>> getLoginToInnMapAsync(Collection<String> customerLogins);
}
