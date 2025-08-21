package com.jjsttk.goodswarehouse.utils;

import com.jjsttk.goodswarehouse.dto.request.NormalizableDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class DtoNormalizer {

    public static void normalize(NormalizableDto dto) {
        dto.setName(NormalizeData.normalizeString(dto.getName()));
        dto.setDescription(NormalizeData.normalizeString(dto.getDescription()));
        dto.setCategory(NormalizeData.normalizeCategory(dto.getCategory()));
        dto.setArticle(dto.getArticle());
        dto.setPrice(NormalizeData.normalizePrice(dto.getPrice()));
        dto.setQuantity(dto.getQuantity());
    }
}