package org.orient.otc.common.core.util;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段别名
 * 在比较相同类型的两个对象的字段发生变更时, 根据此注解获取字段中文名称
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAlias {
	/**
	 * 别名值
	 * @return
	 */
	String value() default "";

	/**
	 * 当前字段是否需要记录入库
	 * @return
	 */
	boolean need() default true;

}
