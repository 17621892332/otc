package org.orient.otc.api.quote.feign;

import org.orient.otc.api.quote.dto.UnderlyingVolatilityFeignDto;
import org.orient.otc.api.quote.dto.VolatilityQueryDto;
import org.orient.otc.api.quote.dto.VolatityQueryCodeListDto;
import org.orient.otc.api.quote.dto.VolatitySaveDto;
import org.orient.otc.api.quote.vo.VolatilityVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

/**
 * @author dzrh
 */
@FeignClient(value = "quoteserver",path = "/volatility", contextId ="volatility")
@Validated
public interface VolatilityClient {

    /**
     * 批量保存波动率信息
     * @param list 波动率对象
     * @return msg
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/saveBatch")
    String saveBatch(@RequestBody List<VolatitySaveDto > list);


    /**
     * 获取有合约的波动率列表
     * @param dto 交易日期
     * @return 合约代码列表
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getUnderlyingCodeListByVol")
    List<String> getUnderlyingCodeListByVol(@RequestBody VolatityQueryCodeListDto dto);

    /**
     * 校验合约是否存在波动率
     * @param volatilityQueryDto 合约代码与交易日期
     * @return true存在波动率false不存在
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/checkHaveVolatility")
    Boolean checkHaveVolatility(@RequestBody VolatilityQueryDto volatilityQueryDto);

    /**
     * 波动率Bankcard保存
     * @param underlyingVolatilityDtoList 合约列表
     * @return 是否成功
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateVolByOffset")
    Boolean updateVolByOffset(@RequestBody List<UnderlyingVolatilityFeignDto> underlyingVolatilityDtoList);

    /**
     * 通过合约代码获取合约的最新波动率
     * @param underlyingCodeSet  合约列表
     * @return 波动率列表
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getVolListByCodeSet")
    List<VolatilityVO> getVolListByCodeSet(@RequestBody Set<String> underlyingCodeSet);

    /**
     * 获所有主力合约波动率
     * @return 波动率列表
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getMainVolList")
    List<VolatilityVO> getMainVolList();
}
