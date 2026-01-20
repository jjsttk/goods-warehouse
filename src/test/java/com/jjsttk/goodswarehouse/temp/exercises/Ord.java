package com.jjsttk.goodswarehouse.temp.exercises;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record Ord(UUID id, List<Prod> prods) {
}
