package com.alex.modelo.validacao;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
 
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = MinIntValidacao.class)
public @interface MinIntValid {
 
    String message() default "{com.alex.modelo.validacao.MinIntValidacao.message}";
    
    int min() default 0;
 
    Class<?>[] groups() default {};
 
    Class<? extends Payload>[] payload() default {};
 
    int value() default 0;
}
