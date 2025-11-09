package com.jjsttk.goodswarehouse.service.product.price.exchange;

import com.jjsttk.goodswarehouse.service.exchange.PriceExchangeService;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.BaseProductServiceDto;
import org.springframework.data.domain.Page;

public interface ProductPriceExchangeService
        extends PriceExchangeService<BaseProductServiceDto, ProductPriceExchangeServiceResponse> {
    @Override
    ProductPriceExchangeServiceResponse exchange(BaseProductServiceDto baseProductServiceDto);

    @Override
    Page<ProductPriceExchangeServiceResponse> exchange(Page<BaseProductServiceDto> baseProductServiceDtoPage);
}
