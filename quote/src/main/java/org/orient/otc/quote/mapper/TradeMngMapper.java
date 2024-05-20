package org.orient.otc.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.orient.otc.quote.dto.trade.TradeConfirmBookQueryDTO;
import org.orient.otc.quote.entity.TradeMng;
import org.orient.otc.quote.vo.daily.TradeMngByDailyVO;
import org.orient.otc.quote.vo.trade.HistoryTradeMngVO;
import org.orient.otc.quote.vo.trade.TradeConfirmBookVO;
import org.orient.otc.quote.vo.trade.TradeProfitLossByClientVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 交易表接口
 */
public interface TradeMngMapper extends BaseMapper<TradeMng> {

    /**
     * 获取客户方向的平仓盈亏
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param clientIdList 客户列表
     * @return 盈亏数据
     */
    List<TradeProfitLossByClientVO> selectProfitLossByClient(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate, @Param("clientIdList") Set<Integer> clientIdList);
    /**
     * 查询区间内存在平仓的交易记录
     * @param page 分页信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param clientIdList 客户列表
     * @return 交易记录
     */
    Page<HistoryTradeMngVO> selectCloseTradeByDateAndClient(Page<HistoryTradeMngVO> page, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("clientIdList") List<Integer> clientIdList);

    /**
     * 查询区间内存在平仓的交易记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param clientIdList 客户列表
     * @return 交易记录
     */
    List<HistoryTradeMngVO> selectCloseTradeByDateAndClient(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("clientIdList") List<Integer> clientIdList);

    /**
     * 获取某天成交的交易记录
     * @param page 分页信息
     * @param queryDate 查询日期
     * @param underlyingCodeList 合约列表
     * @param clientIdList 客户列表
     * @return 分页信息
     */
    Page<TradeMngByDailyVO> selectTradeMngByDaily(Page<TradeMngByDailyVO> page, @Param("queryDate") LocalDate queryDate,@Param("underlyingCodeList") List<String> underlyingCodeList,@Param("clientIdList") List<Integer> clientIdList);

    /**
     * 获取某天成交的交易记录
     * @param queryDate 查询日期
     * @param underlyingCodeList 合约列表
     * @param clientIdList 客户列表
     * @return 交易记录
     */
    List<TradeMngByDailyVO> selectTradeMngByDaily( @Param("queryDate") LocalDate queryDate,@Param("underlyingCodeList") List<String> underlyingCodeList,@Param("clientIdList")  List<Integer> clientIdList);

    /**
     * 查询交易确认书数据
     * @param page 分页信息
     * @param dto 查询条件
     * @return 查询结果
     */
    Page<TradeConfirmBookVO> selectTradeConfirmBook(Page<TradeConfirmBookVO> page, TradeConfirmBookQueryDTO dto);
}
