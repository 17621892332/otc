package org.orient.otc.quote.service;

import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.quote.entity.ObsTradeDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 结算服务
 */
public interface SettlementService {

    /**
     * 获取场内今日所有的交易记录
     * @return 结算结果
     */
    Boolean getExchangeTrade();

    /**
     * 获取场内持仓
     * @return 结算结果
     */
    Boolean getExchangePosition();

    /**
     * 更新结算日期的累计期权观察价格
     * @param settlementDate 结算日期
     * @return 结算结果
     */
    SettlementVO updateTradeObsDatePrice(LocalDate settlementDate);

    /**
     * 累计期权生成远期期权
     * @param tradeId    累计期权ID
     * @param tradeDate  交易日期
     * @param closePrice 收盘价格
     * @return 远期期权信息
     */
    ObsTradeDetail cumulativeGenerateForward(Integer tradeId, LocalDate tradeDate, BigDecimal closePrice);



    /**
     * 保存风险管理收益列表
     * @param settlementDto 交易日期
     * @return 结算结果
     */
    SettlementVO saveTradeRiskInfo(SettlementDTO settlementDto);

    /**
     * 计算交易保证金
     * @param list 交易数据
     * @param settlementDate 结算日期
     * @return key 交易编号 value 保证金
     */
    Map<String, BigDecimal> getTradeMargin(List<TradeMngVO> list, LocalDate settlementDate);

    /**
     * 计算交易保证金
     * @param list 交易数据
     * @return key 交易编号 value 保证金
     */
    Map<String, BigDecimal> getTradeNowMargin(List<TradeMngVO> list);
    /**
     * 保存场内风险管理收益列表
     * @param settlementDto settlementDto
     * @return 结算结果
     */
    SettlementVO saveExchangeTradeRiskInfo(SettlementDTO settlementDto);

    /**
     * 校验今日持仓
     * @return 结算结果
     */
    Boolean checkTodayCaclPos();

    /**
     * 更新场内持仓信息
     * @param settlementDto settlementDto
     * @return 结算结果
     */
    SettlementVO updateTodayPosData(SettlementDTO settlementDto);

    /**
     * 初始化下一个交易日的持仓
     * @return 结算结果
     */
    SettlementVO copyPosDataToNextTradeDay();

    /**
     * 获取今日持仓校验结果
     * @return 结算结果
     */
    SettlementVO getCheckTodayPosResult();

    /**
     * 校验累计期权是否全部已观察
     * @param settlementDate 结算日期
     * @return true 已观察 false 未观察
     */
    Boolean checkObsStatus(LocalDate settlementDate);

    /**
     * 更新敲入标识
     * 收盘价 < 敲入价格 , 更新雪球的敲入标识
     * @param settlementDto 结算日期
     * @return 结算结果
     */
    SettlementVO updateKnockedIn(SettlementDTO settlementDto);

    /**
     * 计算已平仓的累计盈亏
     * @return 保存结果
     */
    SettlementVO saveCloseTradeTotalPnl();

}
