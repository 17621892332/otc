package org.orient.otc.api.feign;

import org.orient.otc.api.dto.BucketedVegaDTO;
import org.orient.otc.api.dto.MarginQuoteDTO;
import org.orient.otc.api.dto.ScenarioDTO;
import org.orient.otc.api.vo.BucketedVegaVO;
import org.orient.otc.api.vo.MarginVO;
import org.orient.otc.api.vo.PythonResult;
import org.orient.otc.api.vo.ScenarioQuoteVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 情景分析
 */
@FeignClient(value = "pyserver"
//        ,url = "http://192.168.60.216:8083"
        ,path = "/", contextId ="pyserver")
public interface PythonClient {
    /**
     * 情景分析
     * @param scenarioDTO 情景分析计算参数
     * @return 计算结果
     */
    @PostMapping("/Scenario")
    PythonResult<ScenarioQuoteVO> scenario(@RequestBody ScenarioDTO scenarioDTO);


    /**
     * 计算保证金
     * @param marginQuoteDTO 情景分析计算参数
     * @return 计算结果
     */
    @PostMapping("/MarginCalc")
    PythonResult<MarginVO> margin(@RequestBody MarginQuoteDTO marginQuoteDTO);

    @PostMapping("/BucketedVega")
    PythonResult<BucketedVegaVO> bucketedVega(@RequestBody BucketedVegaDTO bucketedVegaDTO);
}
