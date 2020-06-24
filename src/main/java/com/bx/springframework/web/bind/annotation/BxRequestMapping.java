package com.bx.springframework.web.bind.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BxRequestMapping {
    String value() default "";
}
