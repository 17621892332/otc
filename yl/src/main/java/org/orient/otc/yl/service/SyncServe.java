package org.orient.otc.yl.service;

import org.orient.otc.api.quote.vo.TradeCloseMngFeignVo;
import org.orient.otc.yl.dto.TradeDelSyncDto;
import org.orient.otc.yl.vo.TradeMngByYlVo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author dzrh
 */
public interface SyncServe {

    /**
     * 从镒链同步合约信息
     * @return true 同步成功 false同步失败
     */
    Boolean syncUnderlying();

    /**
     * 从镒链同步客户信息
     * @return true 同步成功 false同步失败
     */
    Boolean syncClient();

    /**
     * 从镒链获取收盘价信息
     * @param startDate   开始日期
     * @param isOnlyToday 是否仅获取当天
     */
    void syncMarketCloseData(LocalDate startDate , Boolean isOnlyToday);

    /**
     * 从镒链同步客户持仓信息
     * @return true 同步成功 false同步失败
     */
    String syncClientPosition();

    /**
     * 从镒链同步单笔交易信息
     * @param assetMap 簿记账户
     * @param traderMap 交易员
     * @param tradeNumber 交易编号
     */
    void syncTradeInfoByTradeCode(Map<String, Integer> assetMap, Map<String, Integer> traderMap, String tradeNumber);

    /**
     * 同步交易记录至镒链
     * @return true 同步成功 false同步失败
     */
    String syncTradeToYl();

    /**
     * 同步交易记录至镒链
     * @param list 交易记录
     * @param isUpdate 是否为更新
     */
    void syncTradeToYl(List<TradeMngByYlVo> list,Boolean isUpdate);

    /**
     * 删除交易记录同步至镒链
     * @param tradeDelSyncDto 交易编号
     */
    void syncTradeDel(TradeDelSyncDto tradeDelSyncDto);

    /**
     * 同步平仓记录至镒链
     * @return 同步消息
     */
    String syncTradeCloseToYl();

    /**
     * 同步平仓信息至镒链
     * @param vo        交易记录
     * @param closeType 平仓类型
     */
    void syncTradeCloseToYl(TradeCloseMngFeignVo vo, String closeType);

    /**
     * 同步保证金信息
     * @param clientId 客户ID
     * @param riskDate 风险日期
     * @return 同步结果
     */
    String syncTradeRiskMargin(Integer clientId, LocalDate riskDate);


    /**
     * 同步风险信息
     * @param clientId 客户ID
     * @param riskDate 风险日期
     * @return 同步结果
     */
    String syncTradeRiskPv(Integer clientId, LocalDate riskDate);
}
