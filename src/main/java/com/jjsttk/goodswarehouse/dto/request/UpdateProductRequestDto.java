package com.jjsttk.goodswarehouse.dto.request;





import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class UpdateProductRequestDto implements NormalizableDto {

    @Nullable
    private String name;

    @Nullable
    private Long article;

    @Nullable
    private String description;

    @Nullable
    private String category;

    @Nullable
    private BigDecimal price;

    @Nullable
    private Integer quantity;
}
