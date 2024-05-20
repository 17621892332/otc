package org.orient.otc.api.system.feign;

import org.orient.otc.api.system.dto.SystemUpdateDTO;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.orient.otc.common.core.vo.SettlementVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FeignClient(value = "systemserver",path = "/system", contextId ="system")
public interface SystemClient {

    /**
     * 切日
     */
    @PostMapping (FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/closeDate")
    List<SettlementVO> closeDate();

    /**
     * 结算
     */
    @PostMapping (FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/settlement")
    List<SettlementVO> settlement(SettlementDTO settlementDto);

    /**
     * 初始化日结日切记录
     */
    @PostMapping (FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/initLog")
    Boolean initLog();

    /**
     * 获取系统配置参数
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/systemInfo")
    Map<String,String> getSystemInfo();

    /**
     * 获取系统交易日
     * @return 系统交易日
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getTradeDay")
    LocalDate getTradeDay();

    /**
     * 获取系统配置参数
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateSystemInfo")
    Boolean updateSystemInfo(@RequestBody SystemUpdateDTO systemUpdateDTO);


}
