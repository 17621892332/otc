package org.orient.otc.dm.feign;

import org.orient.otc.api.dm.dto.CalendarPropertyQueryDto;
import org.orient.otc.api.dm.dto.CalendarStartEndDto;
import org.orient.otc.api.dm.dto.TradayAddDaysDto;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.dm.vo.CalendarProperty;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.dm.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/calendar")
public class CalendarFeignController implements CalendarClient {
    @Autowired
    private CalendarService calendarService;

    @Override
    public Boolean isTradeDay(LocalDate date) {
        return calendarService.isTraday(date);
    }

    @Override
    public List<LocalDate> getTradeDateList(CalendarStartEndDto calendarStartEndDto) {
        return calendarService.getTradeDateList(calendarStartEndDto);
    }

    @Override
    public CalendarProperty getCalendarProperty(@RequestBody @Valid CalendarPropertyQueryDto calendarQueryDto) {
        return calendarService.getCalendarProperty(calendarQueryDto);
    }

    @Override
    public LocalDate tradeDayAddDays(@RequestBody @Valid TradayAddDaysDto tradayAddDaysDto) {
        return calendarService.tradeDayAddDays(tradayAddDaysDto.getDate(),tradayAddDaysDto.getDays());
    }

    @Override
    public SettlementVO gotoNextTradeDay() {
        return calendarService.gotoNextTradeDay();
    }

    @Override
    public LocalDate getLastTradeDay(LocalDate day) {
        return calendarService.getLastTradeDay(day);
    }
}
