package org.orient.otc.quote.config.multidb;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 切换数据源
 * 选择非master数据源时, 通过切面切换数据源
 * @author dzrh
 */
@Aspect
@Component
@Order(1)
public class DynamicDataSourceAspect {

    /**
     * 切入点只对@Service注解的类上的@Db注解生效 , 无注解 , 默认使用master
     * @param db
     */
    @Pointcut(value="@within(org.springframework.stereotype.Service) && @annotation(db)" )
    public void dynamicDataSourcePointCut(DB db){}

    @Before(value = "dynamicDataSourcePointCut(db)", argNames = "db")
    public void switchDataSource(DB db) {
        DynamicDataSourceSwitcher.setDataSource(db.value());
    }

    /**
     * 切点执行完后 切换成主数据库
     * @param db
     */
    @After(value="dynamicDataSourcePointCut(db)", argNames = "db")
    public void after(DB db){
        DynamicDataSourceSwitcher.cleanDataSource();
    }
}
