package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.api.quote.vo.TradeCloseMngFeignVo;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.quote.dto.settlementReport.SettlementReportDTO;
import org.orient.otc.quote.dto.trade.RollbackTradeCloseMngDTO;
import org.orient.otc.quote.dto.trade.TradeCloseInsertDTO;
import org.orient.otc.quote.dto.trade.TradeCloseQueryDto;
import org.orient.otc.quote.entity.TradeCloseMng;
import org.orient.otc.quote.vo.trade.HistoryTradeMngVO;
import org.orient.otc.quote.vo.trade.TradeCloseMngVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 平仓服务
 */
public interface TradeCloseMngService extends IServicePlus<TradeCloseMng> {

    /**
     * 平仓记录录入
     * @param tradeCloseInsertDto 平仓信息
     * @return 录入后的信息
     */
    List<TradeCloseMng> insertTradeClose(TradeCloseInsertDTO tradeCloseInsertDto);

    /**
     * 批量保存镒链同步回来的平仓记录
     * @param list 镒链转换的对象列表
     * @return 是否成功
     */
    boolean saveOrUpdateByCode(List<TradeCloseMngFeignVo> list);

    /**
     * 获取未同步的交易记录
     * @return 平仓记录
     */
    List<TradeCloseMngFeignVo> queryNotSyncTradeList();

    /**
     * 通过组合代码获取平仓信息
     * @param tradeCloseQueryDto 组合代码
     * @return 平仓信息
     */
    List<TradeCloseMngVO> getTradeCloseMngInfoByCombCode(TradeCloseQueryDto tradeCloseQueryDto);


    /**
     * 获取指定日期平仓数据
     * @param closeDate 交易日期
     * @return 平仓信息
     */
    List<TradeCloseMng> getTradeCloseMngByDate(LocalDate closeDate);

    /**
     * 平仓回退
     * @param rollbackTradeCloseMngDto 回退ID
     * @return 回退结果
     */
    String rollbackTradeCloseMng(RollbackTradeCloseMngDTO rollbackTradeCloseMngDto);

    /**
     * 查询区间内发生平仓的历史交易记录
     * @param dto 开始结束日期
     * @return 交易记录
     */
    Page<HistoryTradeMngVO> historyTradeByPage(SettlementReportDTO dto);
    /**
     * 查询区间内发生平仓的历史交易记录
     * @param dto 开始结束日期
     * @return 交易记录
     */
    List<HistoryTradeMngVO> historyTrade(SettlementReportDTO dto);
}
