package org.orient.otc.quote.config.multidb;


import com.alibaba.druid.util.StringUtils;


/**
 * @author dzrh
 */
public class DynamicDataSourceSwitcher {

    /**
     * 多数据源名称定义
     */
    public static final String MASTER = "master";
    public static final String SLAVE = "slave";

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setDataSource(String name){
        CONTEXT_HOLDER.set(name);
    }

    public static String getDataSource(){
        if (StringUtils.isEmpty(CONTEXT_HOLDER.get())) {
            setDataSource(MASTER);
        }
        return CONTEXT_HOLDER.get();
    }

    public static void cleanDataSource(){
        CONTEXT_HOLDER.remove();
    }

}
