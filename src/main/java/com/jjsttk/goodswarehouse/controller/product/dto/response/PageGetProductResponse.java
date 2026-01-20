package com.jjsttk.goodswarehouse.controller.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Generic paginated response for product data")
public record PageGetProductResponse<T>(

        @Schema(
                description = "List of products on the current page"
        )
        List<T> content,

        @Schema(
                description = "Total number of items in all pages",
                example = "125"
        )
        Long totalCount,

        @Schema(
                description = "Total number of available pages",
                example = "7"
        )
        Integer totalPages,

        @Schema(
                description = "Current page number. Zero based",
                example = "2"
        )
        Integer currentPage,

        @Schema(
                description = "Requested page size (limit)",
                example = "20"
        )
        Integer pageSize,

        @Schema(
                description = "Number of items actually present on the current page",
                example = "15"
        )
        Integer currentPageSize
) {
}
