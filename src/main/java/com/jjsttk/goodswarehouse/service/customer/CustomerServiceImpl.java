package com.jjsttk.goodswarehouse.service.customer;

import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.mapper.customer.spring.ConversionServiceCustomerMapper;
import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.persistence.repository.CustomerRepository;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerInfoDto;
import com.jjsttk.goodswarehouse.service.customer.integration.account.AccountService;
import com.jjsttk.goodswarehouse.service.customer.integration.inn.InnService;
import com.jjsttk.goodswarehouse.service.order.dto.internal.CustomerExternalData;
import com.jjsttk.goodswarehouse.service.order.dto.internal.CustomerExternalDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;
    private final ConversionServiceCustomerMapper mapper;
    private final InnService innService;
    private final AccountService accountService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public BaseCustomerInfoDto getById(Long id) {
        var mbCustomer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerEntity.class, id));
        return mapper.mapToServiceResponse(mbCustomer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CustomerExternalData getExternalDataByLogins(Map<Long, String> idLoginMap) {
        var logins = idLoginMap.values();  // unique

        log.info("Logins: {}", logins);

        var innsMapCf = innService.getLoginToInnMapAsync(logins);
        var accNumsMapCf = accountService.getAccountNumbersMapAsync(logins);

        try {
            return CompletableFuture.allOf(innsMapCf, accNumsMapCf)
                    .thenApply(v -> {
                        var innsMap = innsMapCf.join();
                        var accNumsMap = accNumsMapCf.join();

                        var infoMap = idLoginMap.entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> new CustomerExternalDataInfo(
                                                innsMap.get(entry.getValue()),
                                                accNumsMap.get(entry.getValue())
                                        )
                                ));

                        return CustomerExternalData.builder()
                                .infoMap(infoMap)
                                .build();
                    })
                    .join();

        } catch (CompletionException completionException) {
            if (completionException.getCause() instanceof RuntimeException originalException) {
                throw originalException;
            }

            throw completionException;
        }
    }
}
