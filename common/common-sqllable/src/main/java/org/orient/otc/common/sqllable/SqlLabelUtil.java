package org.orient.otc.common.sqllable;

import java.util.Map;

public class SqlLabelUtil {
    public static SqlMeta getSqlMeta(String sql, Map param){

        Configuration configuration = new Configuration();

        SqlTemplate template = configuration
                .getTemplate(sql);

        SqlMeta process = template.process(param);
        return process;
    }
}
