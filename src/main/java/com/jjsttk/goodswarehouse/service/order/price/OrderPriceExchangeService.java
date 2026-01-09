package com.jjsttk.goodswarehouse.service.order.price;

import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderServiceResponse;
import com.jjsttk.goodswarehouse.service.order.price.dto.response.OrderPriceExchangeServiceResponse;

/**
 * Service interface for handling order price currency synchronization.
 * <p>
 * This service acts as a bridge between order management and the exchange rate system,
 * ensuring that order costs are correctly reflected in the user's preferred currency.
 */
public interface OrderPriceExchangeService {

    /**
     * Recalculates all price-related fields within an order based on current exchange rates.
     * <p>
     * This process involves converting the unit prices of individual items as well as
     * the total order amount from the base system currency to the target session currency.
     *
     * @param baseOrderServiceResponse the source order data containing prices in the base currency.
     * @return an {@link OrderPriceExchangeServiceResponse} containing the recalculated prices
     * and the target currency.
     */
    OrderPriceExchangeServiceResponse exchange(BaseOrderServiceResponse baseOrderServiceResponse);
}
