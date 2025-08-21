package com.jjsttk.goodswarehouse.controller.request.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotBlankOrNullValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankOrNull {
    String message() default "Field cannot be empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
