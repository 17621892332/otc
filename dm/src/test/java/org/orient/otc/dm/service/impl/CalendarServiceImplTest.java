package org.orient.otc.dm.service.impl;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.api.dm.dto.CalendarAddDto;
import org.orient.otc.api.dm.dto.CalendarDetailDto;
import org.orient.otc.dm.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CalendarServiceImplTest extends TestCase {

    @Autowired
    CalendarService calendarService;

    @Test
    public void addCalendarTest() throws Exception {
        List<CalendarAddDto> dtos = new ArrayList<>();
        CalendarAddDto dto1 = new CalendarAddDto();
        dto1.setYear(2028);
        dto1.setDate(LocalDate.now().minusYears(-5));
        dto1.setIsHoliday("weekday");
        dto1.setIsWeekend("noweekend");
        dto1.setIsTradingDay("tradingday");
        dtos.add(dto1);
        calendarService.addCalendar(dtos);
    }
    @Test
    public void getDetailTest(){
        CalendarDetailDto dto = new CalendarDetailDto();
        dto.setYear(2028);
        System.out.println("========="+calendarService.getCalendarDetail(dto));
    }

}
