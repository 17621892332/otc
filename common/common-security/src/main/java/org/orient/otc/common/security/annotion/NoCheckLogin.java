package org.orient.otc.common.security.annotion;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NoCheckLogin {
}
