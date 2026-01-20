package com.jjsttk.goodswarehouse.service.customer;

import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerInfoDto;

/**
 * Service interface for customer management operations.
 * Provides methods for retrieving customer information and performing
 * customer-related validations.
 */
public interface CustomerService {

    /**
     * Retrieves customer details by ID.
     * <p>
     * Returns complete customer information including status, contact details,
     * and other relevant attributes.
     * </p>
     *
     * @param id the customer's unique identifier (primary key)
     * @return customer data transfer object containing customer details
     * @throws ResourceNotFoundException if customer with given ID doesn't exist
     */
    BaseCustomerInfoDto getById(Long id);

    /**
     * Verifies customer existence.
     * <p>
     * Efficient method to check if a customer record exists without
     * loading the entire entity. Useful for validation before operations.
     * </p>
     *
     * @param id the customer identifier to verify
     * @return {@code true} if customer exists, {@code false} otherwise
     */
    Boolean existsById(Long id);
}
