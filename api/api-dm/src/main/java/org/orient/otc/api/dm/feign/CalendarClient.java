package org.orient.otc.api.dm.feign;

import org.orient.otc.api.dm.dto.CalendarPropertyQueryDto;
import org.orient.otc.api.dm.dto.CalendarStartEndDto;
import org.orient.otc.api.dm.dto.TradayAddDaysDto;
import org.orient.otc.api.dm.vo.CalendarProperty;
import org.orient.otc.common.core.feign.FeignConfig;
import org.orient.otc.common.core.vo.SettlementVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@FeignClient(value = "dmserver",path = "/calendar", contextId ="calendar")
public interface CalendarClient {

    /**
     * 判断当前日期是否为交易日
     * @param date 日期
     * @return true 交易日 false 非交易日
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/isTradeDay")
    Boolean isTradeDay(@RequestParam LocalDate date);

    /**
     * 获取某个日期区间内的交易日
     * @param calendarStartEndDto 开始日期与结束日期
     * @return 区间交易日
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getTradeDateList")
    List<LocalDate> getTradeDateList(@RequestBody CalendarStartEndDto calendarStartEndDto);
    /**
     * 获取一段时间内交易日工作日假日ttm
     * @param calendarQueryDto 开始日期与结束日期
     * @return TTM等数据
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getCalendarProperty")
    CalendarProperty getCalendarProperty(@RequestBody @Valid CalendarPropertyQueryDto calendarQueryDto);

    /**
     * 获取指定交易日
     * @param tradayAddDaysDto 交易日与天数
     * @return 交易日
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/tradeDayAddDays")
    LocalDate tradeDayAddDays(@RequestBody @Valid TradayAddDaysDto tradayAddDaysDto);

    /**
     * 将系统交易日期更新为下一个工作日
     * @return 更新信息
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/gotoNextTradeDay")
    SettlementVO gotoNextTradeDay();

    /**
     * 获取当前交易日的上一个交易日
     * @return 更新信息
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getLastTradeDay")
    LocalDate getLastTradeDay(@RequestParam LocalDate day);

}
