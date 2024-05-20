package org.orient.otc.openapi.feign;

import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.openapi.feign.TransDetailClient;
import org.orient.otc.api.openapi.vo.TransDetailVo;
import org.orient.otc.openapi.dto.StatusConvert;
import org.orient.otc.openapi.service.TransDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transDetail")
@Slf4j
public class TransDetailFeignController implements TransDetailClient {
    @Autowired
    private TransDetailService transDetailService;


    @Override
    public List<TransDetailVo> getTransDetail() {
       return transDetailService.getTransDetailList();
    }
    @Override
    public Boolean statusConvertY(String id) {
        StatusConvert statusConvert = StatusConvert.createWithId(id);
        return transDetailService.statusConvertY(statusConvert);
    }
    @Override
    public Boolean statusConvertN(String id) {
        StatusConvert statusConvert = StatusConvert.createWithId(id);
        return transDetailService.statusConvertN(statusConvert);
    }
}
