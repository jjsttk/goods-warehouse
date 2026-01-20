package com.jjsttk.goodswarehouse.shared.enums.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

@Schema(description = "Filter operation")
public enum FilterOperation {

    @Schema(description = "Equal")
    EQUAL("==", "eq", "equal"),

    @Schema(description = "GreaterThanOrEqual")
    GREATER_THAN_OR_EQUAL(">=", "gtOrEq", "greaterThanOrEqual"),

    @Schema(description = "LessThanOrEqual")
    LESS_THAN_OR_EQUAL("<=", "ltOrEq", "lessThanOrEqual"),

    @Schema(description = "iLike")
    LIKE("~", "like", "iLike");

    private final String symbol;       // main
    private final String[] aliases;    // alternative


    FilterOperation(String operationSymbol, String... operationSymbolAlias) {
        this.symbol = operationSymbol;
        this.aliases = operationSymbolAlias;
    }

    @JsonCreator
    public static @Nullable FilterOperation fromString(@Nullable String value) {
        if (value == null) {
            return null;
        }

        var normalized = value.strip();

        for (FilterOperation op : values()) {
            if (op.symbol.equalsIgnoreCase(normalized)) {
                return op;
            }
            for (String alias : op.aliases) {
                if (alias.equalsIgnoreCase(normalized)) {
                    return op;
                }
            }
        }
        throw new IllegalArgumentException("Unknown filter operation: " + value);
    }
}
