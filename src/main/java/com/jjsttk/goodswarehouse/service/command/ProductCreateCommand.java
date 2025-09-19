package com.jjsttk.goodswarehouse.service.command;

import com.jjsttk.goodswarehouse.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ProductCreateCommand {
    private String name;
    private String article;
    private String description;
    private Category category;
    private BigDecimal price;
    private BigDecimal quantity;
}
