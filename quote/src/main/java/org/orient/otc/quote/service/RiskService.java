package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.quote.dto.TradeRiskInfoDto;
import org.orient.otc.quote.dto.risk.PositionPageListDto;
import org.orient.otc.quote.entity.ExchangeTrade;
import org.orient.otc.quote.enums.OpenOrCloseEnum;
import org.orient.otc.quote.vo.PositionPageListVo;
import org.orient.otc.quote.vo.TradeRiskInfoExportVo;
import org.orient.otc.quote.vo.TradeRiskInfoVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 风险服务
 * @author chengqiang
 */
public interface RiskService {

    /**
     * 初始化持仓
     * @param preTrdDay 前一个交易日
     * @return true 初始化成功 false 初始化失败
     */
    Boolean initPosData(String preTrdDay);

    /**
     * 复制场内持仓
     * @return 是否成功
     */
    Boolean copyPosDataToNextTradeDay();

    /**
     * 保存今日持仓信息
     * @param tradeDay 持仓日期
     * @return 结算结果
     */
    SettlementVO updatePosData(String tradeDay);

    /**
     * 重置开平仓金额数据

     * @return 是否重置成功
     */
     String setTodayOpenAndClose();

    /**
     * 获取某个交易日的开平仓金额
     * @param tradeDay 交易日期
     * @return key
     */
     Map<OpenOrCloseEnum, Map<String, BigDecimal>> getOpenAndClose(LocalDate tradeDay);
    /**
     * 重新计算持仓
     * @return 重算结果
     */
    Boolean reCalculationPos();

    /**
     * 计算持仓
     * @param tradeData 交易记录
     */
    void calcPos(ExchangeTrade tradeData);

    /**
     * 校验exchangePositionTmp和redis中的持仓是否一致
     * @param exchangeAccount 场内账号
     */
    void checkExchangePosByAccount(String exchangeAccount);
    /**
     * 校验exchangePositionTmp和redis中的持仓是否一致
     */
    void checkExchangePos();

    /**
     * 将临时表的持仓数据加载到正式表中
     * @return 复制结果
     */
    Boolean fromTmpToExchangePosition();

    /**
     * 将临时表的交易记录加载到正式表中
     * @return 复制结果
     */
    Boolean fromTmpToExchangeTrade();

    IPage<TradeRiskInfoVo> selectListByPage(TradeRiskInfoDto dto);

    List<TradeRiskInfoExportVo> getExportData(TradeRiskInfoDto dto);

    IPage<PositionPageListVo> selectPosListByPage(PositionPageListDto dto) throws Exception;

    void exportPos(PositionPageListDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception;

    void getExportDefinitionRisk(TradeRiskInfoDto dto,HttpServletRequest request, HttpServletResponse response);
}
