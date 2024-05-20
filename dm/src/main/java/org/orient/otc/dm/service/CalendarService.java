package org.orient.otc.dm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.api.dm.dto.*;
import org.orient.otc.api.dm.vo.CalendarProperty;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.dm.entity.Calendar;
import org.orient.otc.dm.vo.CalendarPageListVo;
import org.orient.otc.dm.vo.CalendarVo;

import java.time.LocalDate;
import java.util.List;

public interface CalendarService extends IServicePlus<Calendar> {

    /**
     * 获取一段时间内交易日工作日假日ttm
     * @param calendarQueryDto 开始日期与结束日期
     * @return TTM等数据
     */
    CalendarProperty getCalendarProperty(CalendarPropertyQueryDto calendarQueryDto);

    /**
     * 批量获取一段时间内交易日工作日假日ttm
     * @param calendarQueryDto 开始日期与结束日期
     * @return TTM等数据
     */
    List<CalendarProperty> getCalendarPropertyBatch(CalendarPropertyBatchQueryDto calendarQueryDto);

    Boolean formateCalendarFromYl(List<Integer> years);

    /**
     * @param calendarQueryDto
     * @return
     */
    List<LocalDate> getTradeDateList(CalendarStartEndDto calendarQueryDto);

    Boolean isTraday(LocalDate date);
    /**
     * 获取n天后的交易日
     * @param tradayAddDaysDto  交易日请求参数
     * @return 目标交易日
     * @apiNote 该接口是以物理日期为维度的获取交易日信息，切日后会有问题，慎重使用
     */
    LocalDate tradayAddDays(TradayAddDaysDto tradayAddDaysDto);
    /**
     * 获取n天后的交易日
     * @param tradeDate 交易日期
     * @param num 天数
     * @return 目标交易日
     */
    LocalDate tradeDayAddDays(LocalDate tradeDate,Integer num);
    /**
     * 根据系统日期获取下一个交易日
     * @param tradeDay 系统日期
     * @return 下一交易日
     */
    LocalDate getNextTradeDay(LocalDate tradeDay );
    SettlementVO gotoNextTradeDay();

    IPage<CalendarPageListVo> selectListByPage(CalendarPageListDto dto);

    String addCalendar(List<CalendarAddDto> dtos) throws Exception;

    String updateCalendar(List<CalendarUpdateDto> dtos) throws Exception;

    CalendarVo getCalendarDetail(CalendarDetailDto dto);

    LocalDate getLastTradeDay(LocalDate day);

    /**
     * 获取系统非交易日
     * @return 非交易日列表
     */
    List<LocalDate> getNotTradeDateList();
}
