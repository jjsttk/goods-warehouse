package com.jjsttk.goodswarehouse.service.product.command;

import com.jjsttk.goodswarehouse.enums.Category;
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
