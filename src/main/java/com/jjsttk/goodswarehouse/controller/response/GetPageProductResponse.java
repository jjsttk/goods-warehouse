package com.jjsttk.goodswarehouse.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@Schema(description = "Generic paginated response for product data")
public final class GetPageProductResponse<T> {

    @Schema(
            description = "List of products on the current page"
    )
    @Builder.Default
    private List<T> content = new ArrayList<>();

    @Schema(
            description = "Total number of items in all pages",
            example = "125"
    )
    private long totalCount;

    @Schema(
            description = "Total number of available pages",
            example = "7"
    )
    private int totalPages;

    @Schema(
            description = "Current page number (starting from 1 if there are results, otherwise 0)",
            example = "2"
    )
    private int currentPage;

    @Schema(
            description = "Requested page size (limit)",
            example = "20"
    )
    private int pageSize;

    @Schema(
            description = "Number of items actually present on the current page",
            example = "15"
    )
    private int currentPageSize;

    public GetPageProductResponse(
            List<T> contentInput, long totalCountInput,
            int totalPagesInput, int currentPageInput,
            int pageSizeInput, int currentPageSizeInput
    ) {
        this.content = Objects.requireNonNullElse(contentInput, Collections.emptyList());
        this.totalCount = totalCountInput;
        this.totalPages = totalPagesInput;
        this.currentPage = currentPageInput;
        this.pageSize = pageSizeInput;
        this.currentPageSize = currentPageSizeInput;
    }
}
