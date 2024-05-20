package org.orient.otc.market.service;

import com.opencsv.exceptions.CsvValidationException;
import org.orient.otc.api.market.dto.CloseDatePriceByDateDto;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.market.entity.MarketCloseData;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 行情服务
 */
public interface MarketService extends IServicePlus<MarketCloseData> {
    /**
     * 获取合约的最后一次收盘价
     * @param underlyingCode 合约代码
     * @return 收盘价
     */
    BigDecimal getSettlementPriceByUnderlyingCode(String underlyingCode);

    /**
     * 日终收盘行情保存
     * @return  保存结果
     */
    SettlementVO saveCloseMarketDate();

    /**
     * 保存镒链收盘价
     * @param list 收盘价数据
     */
    void loadYlCloseMarketData(List<MarketCloseData> list);

    /**
     * 获取某个合约的所有日期的收盘价
     * @param underlyingCode  合约代码
     * @return key 收盘日期 value收盘价
     */
    Map<String, BigDecimal> getCloseDatePriceByCode(String underlyingCode);

    /**
     *  通过合约代码获取收盘价
     * @param dto 合约代码
     * @return key 合约代码 value 收盘价
     */
    Map<String,BigDecimal> getClosePrice(CloseDatePriceByDateDto dto);

    /**
     * 获取某天的所有合约的收盘价
     * @param closeDate  收盘日期格式: yyyyMMdd
     * @return key 合约代码 value收盘价
     * @apiNote 返回的合约代码均会转为大写
     */
    Map<String, BigDecimal> getCloseMarketDataByDate(LocalDate closeDate);

    /**
     * 获取指数行情
     * @throws IOException 异常
     * @throws CsvValidationException 异常
     */
    void getShareMarket() throws IOException, CsvValidationException;
    /**
     * 获取某天的所有合约的结算价
     * @param closeDate  收盘日期格式: yyyyMMdd
     * @return key 合约代码 value结算价
     * @apiNote 返回的合约代码均会转为大写
     */
    Map<String, BigDecimal> getSettlementMarketDataByDate(String closeDate);
}
