package com.jjsttk.goodswarehouse.controller.request.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import com.jjsttk.goodswarehouse.utils.ExceptionMessage;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;

@Component
@RequiredArgsConstructor
public final class UniqueArticleValidator implements ConstraintValidator<UniqueArticle, String> {

    private final ProductRepository productRepository;

    @Override
    public boolean isValid(String article, ConstraintValidatorContext context) {
        if (article == null || article.isBlank()) {
            return true;
        }

        var product = productRepository.findByArticle(article);

        if (product.isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    ExceptionMessage.entityWithThisIdAlreadyUsesThisArticleMessage(
                            ProductEntity.class, product.get().getId()
                    )
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
