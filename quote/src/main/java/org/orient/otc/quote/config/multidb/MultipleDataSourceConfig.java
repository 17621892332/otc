package org.orient.otc.quote.config.multidb;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dzrh
 */
@Configuration
public class MultipleDataSourceConfig {
    /**
     * 主数据源配置
     * prefix 必须和nacos中保持一致
     * @return
     */
    @Bean(DynamicDataSourceSwitcher.MASTER)
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource createMasterDataSource(){
        return new DruidDataSource();
    }

    /**
     * 其他数据源
     * @return
     */
    @Bean(DynamicDataSourceSwitcher.SLAVE)
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource createSlave1DataSource(){
        return new DruidDataSource();
    }

    /**
     * 设置动态数据源，添加上面配置的多数据源, 通过@Primary 来确定主DataSource
     * @return
     */
    @Bean
    @Primary
    public DataSource createDynamicDataSource(@Qualifier(DynamicDataSourceSwitcher.MASTER) DataSource master,
                                              @Qualifier(DynamicDataSourceSwitcher.SLAVE) DataSource slave){
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        //设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(master);
        //配置多数据源
        Map<Object, Object> map = new HashMap<>();
        map.put(DynamicDataSourceSwitcher.MASTER,master);
        map.put(DynamicDataSourceSwitcher.SLAVE,slave);
        dynamicDataSource.setTargetDataSources(map);
        return  dynamicDataSource;
    }
}
