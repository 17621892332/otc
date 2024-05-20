package org.orient.otc.common.core.config;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.lang.annotation.*;
import java.math.RoundingMode;

/**
 * 小数格式化类型
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@Documented
public @interface BigDecimalFormatter {

    /**
     * @return 格式化
     */
    String pattern() default "###.##";

    /**
     *
     * @return 返回类别
     */
    JsonFormat.Shape shape() default JsonFormat.Shape.NUMBER;

    /**
     *
     * @return 保留小数位
     */
    int newScale() default 2;

    /**
     *
     * @return 舍入方式
     */
    RoundingMode roundingMode() default RoundingMode.HALF_UP;

}

