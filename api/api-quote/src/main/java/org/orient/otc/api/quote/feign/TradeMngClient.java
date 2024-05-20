package org.orient.otc.api.quote.feign;

import org.orient.otc.api.quote.dto.ProfitLossAppraisementDto;
import org.orient.otc.api.quote.dto.RiskVolUpdateDto;
import org.orient.otc.api.quote.dto.SyncUpdateDto;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dzrh
 */
@FeignClient(value = "quoteserver",path = "/trade", contextId ="trade")
public interface TradeMngClient {
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getTraderById")
    TradeMngVO getTraderById(@RequestParam Integer id);

    /**
     * 通过交易编号获取交易记录
     * @param tradeCode 交易编号
     * @return 交易记录
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getTraderByTradeCode")
    TradeMngVO getTraderByTradeCode(@RequestParam String tradeCode);
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getNotSyncTradeList")
    List<TradeMngVO> getNotSyncTradeList();

    /**
     * 保存镒链同步回来的交易记录
     * @param TradeMngVO 交易记录
     * @return 保存结果
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/saveTradeByYl")
    Boolean saveTradeByYl(@RequestBody TradeMngVO TradeMngVO);
    /**
     * 更新同步交易记录至镒链的同步状态
     * @param dto 请求对象
     * @return true 更新成功 false 更新失败
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateSync")
    Boolean updateSync(@RequestBody SyncUpdateDto dto);

    /**
     * 更新风险波动率覆盖
     * @param riskVolUpdateDto 波动率
     * @return 覆盖结果
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateRiskVol")
    Boolean updateRiskVol(@RequestBody  RiskVolUpdateDto riskVolUpdateDto);

    /**
     * 统计区间内 , 指定客户 , 发生平仓/部分平仓的总盈亏
     * @param dto
     * @return
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getRealizeProfitLoss")
    BigDecimal getRealizeProfitLoss(@RequestBody ProfitLossAppraisementDto dto);
}
