package com.jjsttk.goodswarehouse.service.customer.integration.account;

import com.jjsttk.goodswarehouse.exception.service.customer.account.AccountServiceException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AccountService {

    /**
     * Asynchronously retrieves a mapping of customer logins to their Account numbers.
     *
     * @param customerLogins collection of customer logins to look up.
     * @return a {@link CompletableFuture} containing a map where the key is the login
     * and the value is the corresponding Account number.
     * @throws AccountServiceException if an error occurs during the retrieval process.
     */
    CompletableFuture<Map<String, String>> getAccountNumbersMapAsync(Collection<String> customerLogins);
}
