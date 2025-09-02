package com.jjsttk.goodswarehouse.controller.request.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueArticleValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueArticle {
    String message() default "Article must be unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
