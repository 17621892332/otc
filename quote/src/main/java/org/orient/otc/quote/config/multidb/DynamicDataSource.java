package org.orient.otc.quote.config.multidb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


/**
 * @author dzrh
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {



    @Override
    protected Object determineCurrentLookupKey() {
//        log.trace("------------------当前数据源 {}", DynamicDataSourceSwitcher.getDataSource());
        return DynamicDataSourceSwitcher.getDataSource();
    }
}
