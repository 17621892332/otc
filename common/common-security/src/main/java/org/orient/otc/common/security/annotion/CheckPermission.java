package org.orient.otc.common.security.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dzrh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CheckPermission {
    /**
     * 权限代码
     * @return 权限码
     */
    String value() default "";

    /**
     * 是否需要校验权限
     * @return true 需要校验 false 不需要校验
     */
    boolean isNeedCheck() default true;
}
