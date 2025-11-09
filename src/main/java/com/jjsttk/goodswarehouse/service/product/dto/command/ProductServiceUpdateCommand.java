package com.jjsttk.goodswarehouse.service.product.dto.command;

import com.jjsttk.goodswarehouse.shared.enums.product.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductServiceUpdateCommand {
    private String name;
    private String article;
    private String description;
    private Category category;
    private BigDecimal price;
    private BigDecimal quantity;
}
