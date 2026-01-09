package com.jjsttk.goodswarehouse.service.product.price.exchange;

import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceProductDetailedResponse;
import org.springframework.data.domain.Page;

public interface ProductPriceExchangeService {

    ProductPriceExchangeServiceResponse exchange(ProductServiceProductDetailedResponse source);

    Page<ProductPriceExchangeServiceResponse> exchange(Page<ProductServiceProductDetailedResponse> source);
}
