package org.orient.otc.common.database.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
//import io.seata.core.context.RootContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeataRequestInterceptor  implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        // 解决seata的xid未传递
//        String xid = RootContext.getXID();
//        if (StringUtils.isNotEmpty(xid)) {
//            template.header(RootContext.KEY_XID, xid);
//        }
    }
}
