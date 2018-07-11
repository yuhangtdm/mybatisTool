package com.dity.mybatis.annotation;

import com.dity.mybatis.enums.IdTypeEnum;

import java.lang.annotation.*;

import static com.dity.mybatis.enums.IdTypeEnum.INTERGER;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableId {
    String value();
    String type() default "INTERGER";
}
