package org.orient.otc.quote.feign;

import org.orient.otc.api.quote.feign.TransDetailClient;
import org.orient.otc.quote.service.TransDetailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 资金记录同步状态
 */
@RestController
@RequestMapping(value = "/transDetailClient")
public class TransDetailFeignController implements TransDetailClient {
    @Resource
    private TransDetailService transDetailService;

    @Override
    public Boolean getTransDetail() {
        if("同步成功".equals(transDetailService.getTransDetail())){
            return true;
        }
        return false;
    }
}
