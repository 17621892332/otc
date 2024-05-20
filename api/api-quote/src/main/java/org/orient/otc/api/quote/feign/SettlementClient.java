package org.orient.otc.api.quote.feign;

import org.orient.otc.api.quote.dto.GetCapitalMonitorMailKeywordsConfigDto;
import org.orient.otc.api.quote.dto.GetSettlementReportMailKeywordsConfigDto;
import org.orient.otc.api.quote.vo.ClientCapitalMonitorVO;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.orient.otc.common.core.vo.SettlementVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 结算相关API
 * @author pjc
 */
@FeignClient(value = "quoteserver",path = "/settlement", contextId ="settlement")
public interface SettlementClient {

    /**
     * 获取是否存在未平仓校验
     * @param settlementDto 结算日期
     * @return 结算结果
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/checkClosedPositionsData")
    List<TradeMngVO> getNeedClosedTradeList(@RequestBody SettlementDTO settlementDto);


    /**
     * 更新累计期权的观察价格
     * @param settlementDto 结算日期
     * @return 结算结果
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateTradeObsDatePrice")
    SettlementVO updateTradeObsDatePrice(@RequestBody SettlementDTO settlementDto) ;

    /**
     * 保存当日持仓风险数据
     * @param settlementDto 结算日期
     * @return 结算结果
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/saveTradeRiskInfo")
    SettlementVO saveTradeRiskInfo(@RequestBody SettlementDTO settlementDto);

    /**
     * 获取场内持仓
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getExchangePosition")
    void getExchangePosition();

    /**
     * 获取场内交易记录
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getExchangeTrade")
    void getExchangeTrade();

    /**
     * 校验当天的持仓结果
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/checkTodayCaclPos")
    void checkTodayCaclPos();

    /**
     * 更正场内补单持仓
     * @param settlementDto 结算日期
     * @return 更正结果
     *
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateTodayPosData")
    SettlementVO updateTodayPosData(@RequestBody @Valid SettlementDTO settlementDto);

    /**
     * 切日初始化场内持仓
     * @return 初始化结果
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/copyPosDataToNextTradeDay")
    SettlementVO copyPosDataToNextTradeDay();

    /**
     * 将波动率数据复制至下一个工作日
     * @param settlementDto 请求对象
     * @return 同步数量
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/saveVolToTradeDay")
    SettlementVO saveVolToTradeDay(@RequestBody @Valid SettlementDTO settlementDto);

    /**
     * 结算检查场内持仓校验结果
     * @return 检查结果
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getCheckTodayPosResult")
    SettlementVO getCheckTodayPosResult();

    /**
     * 校验累计期权是否均已观察
     * @param settlementDto 结算日期
     * @return ture 校验通过 false校验失败
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/checkObsStatus")
    Boolean checkObsStatus(@RequestBody SettlementDTO settlementDto);

    /**
     * 更新敲入标识
     * @param settlementDto 结算日期
     * @return ture 校验通过 false校验失败
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateKnockedIn")
    SettlementVO updateKnockedIn(@RequestBody SettlementDTO settlementDto);


    /**
     * 计算已平仓的累计盈亏
     * @return 保存结果
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/saveCloseTradeTotalPnl")
    SettlementVO saveCloseTradeTotalPnl();

    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/saveCloseTradeTotalPnl")
    ClientCapitalMonitorVO getCapitalMonitorByClientId(@RequestParam Integer clientId);

    /**
     * 资金监控中获取邮件通配符结果-批量发送和重发使用
     * @param dto 入参
     * @return 返回值
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getCapitalMonitorMailKeywordsConfig")
    Map<String, String> getCapitalMonitorMailKeywordsConfig(@RequestBody GetCapitalMonitorMailKeywordsConfigDto dto);
    /**
     * 结算报告中获取邮件通配符结果-重发使用
     * @param dto 入参
     * @return 返回值
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getSettlementReportMailKeywordsConfig")
    Map<String, String> getSettlementReportMailKeywordsConfig(@RequestBody GetSettlementReportMailKeywordsConfigDto dto);
}
