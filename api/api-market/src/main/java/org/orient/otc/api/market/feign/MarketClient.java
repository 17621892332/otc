package org.orient.otc.api.market.feign;

import org.orient.otc.api.market.dto.CloseDatePriceByDateDto;
import org.orient.otc.api.market.dto.MarketCloseDataSaveDto;
import org.orient.otc.api.market.dto.MarketCodeDto;
import org.orient.otc.api.market.vo.MarketInfoVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.orient.otc.common.core.vo.SettlementVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 行情服务对内接口
 */
@FeignClient(value = "marketserver",path = "/market", contextId ="market")
public interface MarketClient {
    /**
     *  获取结算价格
     * @param underlyingCode 合约代码
     * @return 结算价格
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getSettlementPriceByUnderlyingCode")
    BigDecimal getSettlementPriceByUnderlyingCode(@RequestParam String underlyingCode);
    /**
     * 获取合约的行情信息
     * @param underlyingCode 合约代码
     * @return 行情信息
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getLastMarketDataByCode")
    MarketInfoVO getLastMarketDataByCode(@RequestParam String underlyingCode);
    /**
     * 获取最新的行情价格
     * @param underlyingCodeList  合约代码
     * @return key 合约代码 value收盘价格
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getLastPriceByUnderlyingCodeList")
    Map<String, BigDecimal> getLastPriceByUnderlyingCodeList(@RequestBody Set<String> underlyingCodeList);
    /**
     * 获取某天的所有收盘价
     * @param dto  交易日期
     * @return key 合约代码 value收盘价格
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getAllCloseDatePriceByCode")
    Map<String, BigDecimal> getAllCloseDatePriceByCode(@RequestBody MarketCodeDto dto);

    /**
     * 日结保存收盘行情信息
     * @return 结算情况
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/saveCloseDate")
    SettlementVO saveCloseMarketDate();

    /**
     * 批量保存行情收盘价信息
     * @param list 收盘价信息
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/loadYlCloseMarketData/")
    void loadYlCloseMarketData(@RequestBody List<MarketCloseDataSaveDto> list);

    /**
     * 获取指定日期的行情
     * @param dto 收盘价参数
     * @return key 合约代码 value收盘价格
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getClosePriceByDateAndCode")
    Map<String,BigDecimal> getClosePriceByDateAndCode(@RequestBody CloseDatePriceByDateDto dto);

    /**
     * 获取某一天的所有合约收盘价
     * @param closeDate 收盘日期
     * @return 收盘价集合 keu 合约代码 value 收盘价
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getCloseMarketDataByDate")
    Map<String, BigDecimal> getCloseMarketDataByDate(@RequestParam LocalDate closeDate);


    /**
     * 获取某一天的所有合约结算价
     * @param closeDate 收盘日期
     * @return 收盘价集合 keu 合约代码 value 结算价
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getSettlementMarketDataByDate")
    Map<String, BigDecimal> getSettlementMarketDataByDate(@RequestParam LocalDate closeDate);
    /**
     * 更新股票行情
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateShareMarket")
    void  updateShareMarket();

}
