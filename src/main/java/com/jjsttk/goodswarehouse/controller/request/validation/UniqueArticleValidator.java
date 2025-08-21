package com.jjsttk.goodswarehouse.controller.request.validation;

import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import com.jjsttk.goodswarehouse.utils.ExceptionMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class UniqueArticleValidator implements ConstraintValidator<UniqueArticle, String> {

    private final ProductRepository productRepository;

    @Override
    public boolean isValid(String article, ConstraintValidatorContext context) {
        if (article == null || article.isBlank()) {
            return true;
        }

        var existingProductId = productRepository.findIdByArticle(article.strip());

        if (existingProductId.isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    ExceptionMessage.entityWithThisIdAlreadyUsesThisArticleMessage(
                            ProductEntity.class, existingProductId.get()
                    )
            ).addConstraintViolation();
            return false;
        }
        return true;
    }
}
