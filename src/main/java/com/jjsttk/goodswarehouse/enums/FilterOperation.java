package com.jjsttk.goodswarehouse.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;

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
    public static FilterOperation fromString(String value) {
        if (value == null) {
            return null;
        }

        for (FilterOperation op : values()) {
            if (op.symbol.equalsIgnoreCase(value)) {
                return op;
            }
            for (String alias : op.aliases) {
                if (alias.equalsIgnoreCase(value)) {
                    return op;
                }
            }
        }
        throw new IllegalArgumentException("Unknown filter operation: " + value);
    }
}
