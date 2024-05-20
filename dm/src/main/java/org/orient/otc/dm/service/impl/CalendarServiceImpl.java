package org.orient.otc.dm.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.dm.dto.*;
import org.orient.otc.api.dm.vo.CalendarProperty;
import org.orient.otc.api.system.dto.SystemUpdateDTO;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.api.system.feign.SystemClient;
import org.orient.otc.api.system.vo.SystemConfigVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.dm.entity.Calendar;
import org.orient.otc.dm.entity.CalendarYl;
import org.orient.otc.dm.enums.IsHolidayEnum;
import org.orient.otc.dm.enums.IsTradingDayEnum;
import org.orient.otc.dm.enums.IsWeekendEnum;
import org.orient.otc.dm.exception.BussinessException;
import org.orient.otc.dm.mapper.CalendarMapper;
import org.orient.otc.dm.mapper.CalendarYlMapper;
import org.orient.otc.dm.service.CalendarService;
import org.orient.otc.dm.vo.CalendarDetailVo;
import org.orient.otc.dm.vo.CalendarPageListVo;
import org.orient.otc.dm.vo.CalendarVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalendarServiceImpl extends ServiceImpl<BaseMapper<Calendar>, Calendar> implements CalendarService {
    @Autowired
    private CalendarMapper calendarMapper;
    @Autowired
    private CalendarYlMapper calendarYlMapper;

    @Autowired
    private SystemClient systemClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public CalendarProperty getCalendarProperty(CalendarPropertyQueryDto calendarQueryDto) {
        CalendarProperty calendarProperty = new CalendarProperty();
        List<Calendar> calendars = calendarMapper.selectList(new LambdaQueryWrapper<Calendar>().ge(Calendar::getDate, calendarQueryDto.getTradeDate()).le(Calendar::getDate, calendarQueryDto.getMaturityDate()).eq(Calendar::getIsDeleted, 0));
        List<Calendar> tradeDays = calendars.stream().filter(calendar -> calendar.getIsTradingDay() == IsTradingDayEnum.tradingday).collect(Collectors.toList());
        calendarProperty.setTradingDay(tradeDays.size());
        List<Calendar> workDays = calendars.stream().filter(calendar -> calendar.getIsWeekend() == IsWeekendEnum.noweekend).collect(Collectors.toList());//非周末
        calendarProperty.setWorkday(workDays.size());
        List<Calendar> holidays = calendars.stream().filter(calendar -> calendar.getIsHoliday() == IsHolidayEnum.holiday && calendar.getIsWeekend() == IsWeekendEnum.noweekend).collect(Collectors.toList());
        calendarProperty.setBankHoliday(holidays.size());
        long until = calendarQueryDto.getTradeDate().until(calendarQueryDto.getMaturityDate(), ChronoUnit.DAYS) + 1;
        calendarProperty.setTtm(BigDecimal.valueOf(until));
        return calendarProperty;
    }

    @Override
    public List<CalendarProperty> getCalendarPropertyBatch(CalendarPropertyBatchQueryDto dto) {
        List<CalendarProperty> returnList = new ArrayList<>();
        // 取入参列表中最小开始日期
        LocalDate minTradeDate = dto.getList().stream().min(Comparator.comparing(CalendarPropertyQueryDto::getTradeDate)).get().getTradeDate();
        // 取入参列表中最大到期日期
        LocalDate maxMaturityDate = dto.getList().stream().max(Comparator.comparing(CalendarPropertyQueryDto::getMaturityDate)).get().getMaturityDate();
        List<Calendar> calendarList = calendarMapper.selectList(
                new LambdaQueryWrapper<Calendar>()
                        .ge(Calendar::getDate, minTradeDate)
                        .le(Calendar::getDate, maxMaturityDate).eq(Calendar::getIsDeleted, 0)
        );
        for (CalendarPropertyQueryDto calendarQueryDto : dto.getList()){
            List<Calendar> calendars = calendarList.stream().filter(item-> !item.getDate().isBefore(calendarQueryDto.getTradeDate()))
                    .filter(item-> !item.getDate().isAfter(calendarQueryDto.getMaturityDate()))
                    .collect(Collectors.toList());
            // 返回对象
            CalendarProperty calendarProperty = new CalendarProperty();
            List<Calendar> tradeDays = calendars.stream().filter(calendar -> calendar.getIsTradingDay() == IsTradingDayEnum.tradingday).collect(Collectors.toList());
            calendarProperty.setTradingDay(tradeDays.size());
            List<Calendar> workDays = calendars.stream().filter(calendar -> calendar.getIsWeekend() == IsWeekendEnum.noweekend).collect(Collectors.toList());//非周末
            calendarProperty.setWorkday(workDays.size());
            List<Calendar> holidays = calendars.stream().filter(calendar -> calendar.getIsHoliday() == IsHolidayEnum.holiday && calendar.getIsWeekend() == IsWeekendEnum.noweekend).collect(Collectors.toList());
            calendarProperty.setBankHoliday(holidays.size());
            long until = calendarQueryDto.getTradeDate().until(calendarQueryDto.getMaturityDate(), ChronoUnit.DAYS) + 1;
            calendarProperty.setTtm(BigDecimal.valueOf(until));
            calendarProperty.setNo(calendarQueryDto.getNo());
            returnList.add(calendarProperty);
        }
        return returnList;
    }

    @Override
    public Boolean formateCalendarFromYl(List<Integer> years) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy,MM,dd");
        for (Integer year : years) {
            LocalDate start = LocalDate.of(year, 1, 1);
            LocalDate end = LocalDate.of(year, 12, 31);
            LocalDate current = start;
            List<LocalDate> dates = new ArrayList<>();
            while (!current.isAfter(end)) {
                dates.add(current);
                current = current.plusDays(1);
            }
            CalendarYl calendarYl = calendarYlMapper.selectOne(new LambdaQueryWrapper<CalendarYl>().eq(CalendarYl::getYear, year));
            List<String> holidayJson = calendarYl.getHolidayJson();
            for (LocalDate d : dates) {
                Calendar calendar = new Calendar();
                calendar.setYear(year);
                calendar.setDate(d);
                String format = d.format(dateTimeFormatter);
                DayOfWeek dayOfWeek = d.getDayOfWeek();
                if (holidayJson.contains(format)) {
                    calendar.setIsHoliday(IsHolidayEnum.holiday);
                    calendar.setIsTradingDay(IsTradingDayEnum.nontradingday);
                } else {
                    calendar.setIsHoliday(IsHolidayEnum.weekday);
                    if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                        calendar.setIsTradingDay(IsTradingDayEnum.nontradingday);
                    } else {
                        calendar.setIsTradingDay(IsTradingDayEnum.tradingday);
                    }
                }
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    calendar.setIsWeekend(IsWeekendEnum.weekend);
                } else {
                    calendar.setIsWeekend(IsWeekendEnum.noweekend);
                }
                Calendar calendar1 = calendarMapper.selectOne(new LambdaQueryWrapper<Calendar>().eq(Calendar::getDate, calendar.getDate()).eq(Calendar::getIsDeleted, 0));
                if (Objects.isNull(calendar1)) {
                    calendarMapper.insert(calendar);
                } else {
                    calendarMapper.update(calendar, new LambdaQueryWrapper<Calendar>().eq(Calendar::getDate, calendar.getDate()));
                }
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public List<LocalDate> getTradeDateList(CalendarStartEndDto calendarQueryDto) {
        List<Calendar> calendars = calendarMapper.selectList(new LambdaQueryWrapper<Calendar>().ge(Calendar::getDate, calendarQueryDto.getStartDate()).le(Calendar::getDate, calendarQueryDto.getEndDate()).eq(Calendar::getIsTradingDay, IsTradingDayEnum.tradingday).eq(Calendar::getIsDeleted, 0));
        return calendars.stream().map(Calendar::getDate).collect(Collectors.toList());
    }

    @Override
    public Boolean isTraday(LocalDate date) {
        Calendar calendar = calendarMapper.selectOne(new LambdaQueryWrapper<Calendar>().eq(Calendar::getDate, date).eq(Calendar::getIsDeleted, 0));
        if (Objects.isNull(calendar)){
            log.error("该日期未维护，默认为交易日:{}",date);
            return Boolean.TRUE;
        }
        if (calendar.getIsTradingDay() == IsTradingDayEnum.tradingday) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    public LocalDate tradeDayAddDays(LocalDate tradeDate,Integer num) {
        List<Calendar> lastTradeCalendar;
        if (num >= 0) {
            lastTradeCalendar = calendarMapper.selectList(new LambdaQueryWrapper<Calendar>()
                    .ge(Calendar::getDate, tradeDate)
                    .eq(Calendar::getIsTradingDay, IsTradingDayEnum.tradingday)
                    .eq(Calendar::getIsDeleted, 0)
                    .orderByAsc(Calendar::getDate)
                    .last("limit " + (num + 1)));
            BussinessException.E_600104.assertTrue(lastTradeCalendar.size() == num + 1);
        } else {
            lastTradeCalendar = calendarMapper.selectList(new LambdaQueryWrapper<Calendar>()
                    .lt(Calendar::getDate, tradeDate)
                    .eq(Calendar::getIsTradingDay, IsTradingDayEnum.tradingday)
                    .eq(Calendar::getIsDeleted, 0).
                    orderByDesc(Calendar::getDate)
                    .last("limit " + (num * -1)));
            BussinessException.E_600104.assertTrue(lastTradeCalendar.size() == num * -1);
        }
        return lastTradeCalendar.get(lastTradeCalendar.size() - 1).getDate();
    }

    @Override
    public LocalDate tradayAddDays(TradayAddDaysDto tradayAddDaysDto) {
        LocalDate tradeDate;
        //如果转入日期为当前时间则取系统交易日
        if (tradayAddDaysDto.getDate().isEqual(LocalDate.now())) {
            String tradeDayStr = Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString();
            if (StringUtils.isNotBlank(tradeDayStr)) {
                tradeDate = LocalDate.parse(tradeDayStr);
            } else {
                tradeDate = LocalDate.now();
            }
        } else {
            tradeDate = tradayAddDaysDto.getDate();
        }
        List<Calendar> lastTradeCalendar;
        if (tradayAddDaysDto.getDays() >= 0) {
            lastTradeCalendar = calendarMapper.selectList(new LambdaQueryWrapper<Calendar>()
                    .ge(Calendar::getDate, tradeDate)
                    .eq(Calendar::getIsTradingDay, IsTradingDayEnum.tradingday)
                    .eq(Calendar::getIsDeleted, 0)
                    .orderByAsc(Calendar::getDate)
                    .last("limit " + (tradayAddDaysDto.getDays() + 1)));
            BussinessException.E_600104.assertTrue(lastTradeCalendar.size() == tradayAddDaysDto.getDays() + 1);
        } else {
            lastTradeCalendar = calendarMapper.selectList(new LambdaQueryWrapper<Calendar>()
                    .lt(Calendar::getDate, tradeDate)
                    .eq(Calendar::getIsTradingDay, IsTradingDayEnum.tradingday)
                    .eq(Calendar::getIsDeleted, 0).
                    orderByDesc(Calendar::getDate)
                    .last("limit " + (tradayAddDaysDto.getDays() * -1)));
            BussinessException.E_600104.assertTrue(lastTradeCalendar.size() == tradayAddDaysDto.getDays() * -1);
        }
        return lastTradeCalendar.get(lastTradeCalendar.size() - 1).getDate();
    }

    @Override
    public LocalDate getNextTradeDay(LocalDate tradeDay) {
        Calendar calendar = calendarMapper.selectOne(new LambdaQueryWrapper<Calendar>()
                .gt(Calendar::getDate, tradeDay)
                .eq(Calendar::getIsTradingDay, IsTradingDayEnum.tradingday)
                .eq(Calendar::getIsDeleted, 0)
                .orderByAsc(Calendar::getDate)
                .last("limit 1"));
        return calendar.getDate();
    }

    @Override
    public SettlementVO gotoNextTradeDay() {
        SettlementVO settlementVo = new SettlementVO();
        StringBuilder stringBuilder = new StringBuilder();
        settlementVo.setIsSuccess(Boolean.TRUE);
        LocalDate tradeDay = LocalDate.now();
        stringBuilder.append("当前工作日为:");
        stringBuilder.append(tradeDay);
        LocalDate nextTradeDay = this.getNextTradeDay(tradeDay);
        stringBuilder.append("下一个工作日为：");
        stringBuilder.append(nextTradeDay);
        //更新系统配置信息
        List<SystemConfigVO> configVOList = new ArrayList<>();
        configVOList.add(SystemConfigVO.builder()
                .configKey(SystemConfigEnum.tradeDay.name())
                .configValue(nextTradeDay.toString())
                .build());
        configVOList.add(SystemConfigVO.builder()
                .configKey(SystemConfigEnum.lastTradeDay.name())
                .configValue(tradeDay.toString())
                .build());
        systemClient.updateSystemInfo(SystemUpdateDTO.builder().configList(configVOList).build());
        settlementVo.setMsg(stringBuilder.toString());
        return settlementVo;
    }

    @Override
    public IPage<CalendarPageListVo> selectListByPage(CalendarPageListDto dto) {

        LambdaQueryWrapper<Calendar> lambdaQueryWrapper = new QueryWrapper<Calendar>()
                .select(" year,count(isTradingDay) as tradingDays ,max(createTime) as createTime").lambda();
        lambdaQueryWrapper.eq(null != dto.getYear(), Calendar::getYear, dto.getYear())
                .groupBy(Calendar::getYear, Calendar::getIsTradingDay).having("isTradingDay=\"tradingday\"", "")
        ;
        IPage<Map<String, Object>> ipage = calendarMapper.selectMapsPage(new Page<>(dto.getPageNo(), dto.getPageSize()), lambdaQueryWrapper);
        IPage<CalendarPageListVo> returnPage = ipage.convert(item -> {
            CalendarPageListVo vo = new CalendarPageListVo();
            vo.setYear(String.valueOf(item.get("year")));
            vo.setTradingDays(Integer.valueOf(String.valueOf(item.get("tradingDays"))));
            vo.setCreateTime(String.valueOf(item.get("createTime")));
            return vo;
        });
        return returnPage;
    }

    @Override
    public String addCalendar(List<CalendarAddDto> dtos) throws Exception {
        int year = dtos.get(0).getYear();
        Set<LocalDate> localDateSet = dtos.stream().map(item -> item.getDate()).collect(Collectors.toSet());
        if (localDateSet.size() < dtos.size()) {
            throw new Exception("同一次添加日历,日历日期不能重复");
        }
        List<Calendar> list = new ArrayList<>();
        for (CalendarAddDto dto : dtos) {
            if (year != dto.getYear()) {
                throw new Exception("同一次添加日历,年份必须相同");
            }
            if (dto.getDate().getYear() != year) {
                throw new Exception("同一次添加日历,年份和日期必须在同一年内");
            }
            Calendar calendar = new Calendar();
            BeanUtils.copyProperties(dto, calendar);
            calendar.setIsWeekend(IsWeekendEnum.valueOf(dto.getIsWeekend()));
            calendar.setIsHoliday(IsHolidayEnum.valueOf(dto.getIsHoliday()));
            calendar.setIsTradingDay(IsTradingDayEnum.valueOf(dto.getIsTradingDay()));
            list.add(calendar);
        }
        // 校验是否存在相同年份,日期的数据
        LambdaQueryWrapper<Calendar> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Calendar::getYear, year)
                .in(Calendar::getDate, list.stream().map(item -> item.getDate()).collect(Collectors.toSet()))
                .eq(Calendar::getIsDeleted, IsDeletedEnum.NO);
        List<Calendar> cList = calendarMapper.selectList(lambdaQueryWrapper);
        if (cList.size() > 0) {
            throw new Exception("本次添加日历中,已存在相同年份,日期的数据,年份:" + year + "日期:" + JSON.toJSON(cList.stream().map(item -> item.getDate()).collect(Collectors.toSet())));
        }
        this.saveBatch(list);
        return "add Calendar success";
    }

    @Override
    public String updateCalendar(List<CalendarUpdateDto> dtos) throws Exception {
        int year = dtos.get(0).getYear();
        Set<LocalDate> localDateSet = dtos.stream().map(item -> item.getDate()).collect(Collectors.toSet());
        if (localDateSet.size() < dtos.size()) {
            throw new Exception("同一次修改日历,日历日期不能重复");
        }
        List<Calendar> list = new ArrayList<>();
        for (CalendarUpdateDto dto : dtos) {
            if (year != dto.getYear()) {
                throw new Exception("同一次修改日历,年份必须相同");
            }
            if (dto.getDate().getYear() != year) {
                throw new Exception("同一次修改日历,年份和日期必须在同一年内");
            }
            Calendar calendar = new Calendar();
            BeanUtils.copyProperties(dto, calendar);
            calendar.setIsWeekend(IsWeekendEnum.valueOf(dto.getIsWeekend()));
            calendar.setIsHoliday(IsHolidayEnum.valueOf(dto.getIsHoliday()));
            calendar.setIsTradingDay(IsTradingDayEnum.valueOf(dto.getIsTradingDay()));
            list.add(calendar);
        }
        this.updateBatchById(list);
        return "update Calendar success";
    }

    @Override
    public CalendarVo getCalendarDetail(CalendarDetailDto dto) {
        CalendarVo vo = new CalendarVo();
        LambdaQueryWrapper<Calendar> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Calendar::getYear, dto.getYear())
                .eq(Calendar::getIsDeleted, IsDeletedEnum.NO);
        List<Calendar> list = calendarMapper.selectList(lambdaQueryWrapper);
        // 组装返回值
        vo.setYear(dto.getYear());
        List<CalendarDetailVo> calendarList = new ArrayList<>();
        list.forEach(calendar -> {
            CalendarDetailVo detailVo = new CalendarDetailVo();
            BeanUtils.copyProperties(calendar, detailVo);
            log.debug("---------" + JSON.toJSONString(calendar));
            detailVo.setIsWeekend(calendar.getIsWeekend().name());
            detailVo.setIsHoliday(calendar.getIsHoliday().name());
            detailVo.setIsTradingDay(calendar.getIsTradingDay().name());
            calendarList.add(detailVo);
        });
        vo.setCalendarList(calendarList);
        return vo;
    }

    @Override
    public LocalDate getLastTradeDay(LocalDate tradeDay) {
        Calendar calendar = calendarMapper.selectOne(new LambdaQueryWrapper<Calendar>()
                .lt(Calendar::getDate, tradeDay)
                .eq(Calendar::getIsTradingDay, IsTradingDayEnum.tradingday)
                .eq(Calendar::getIsDeleted, 0)
                .orderByDesc(Calendar::getDate)
                .last("limit 1"));
        return calendar.getDate();
    }

    @Override
    public List<LocalDate> getNotTradeDateList() {
        List<Calendar> calendars = calendarMapper.selectList(new LambdaQueryWrapper<Calendar>()
                .eq(Calendar::getIsTradingDay, IsTradingDayEnum.nontradingday).eq(Calendar::getIsDeleted, 0));
        return calendars.stream().map(Calendar::getDate).collect(Collectors.toList());
    }
}
