package org.orient.otc.dm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.api.dm.dto.*;
import org.orient.otc.api.dm.vo.CalendarProperty;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.dm.service.CalendarService;
import org.orient.otc.dm.vo.CalendarPageListVo;
import org.orient.otc.dm.vo.CalendarVo;
import org.orient.otc.dm.vo.NotTradeVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * 日期相关接口
 */
@RestController
@RequestMapping("/calendar")
@Api(tags = "日期")
public class CalendarController {
    @Resource
    private CalendarService calendarService;

    @Resource
    private SystemConfigUtil systemConfigUtil;
    @ApiOperation("将镒链的日历转为自己的日历")
    @PostMapping("/formateCalendarFromYl")
    public HttpResourceResponse<Boolean> formateCalendarFromYl(@RequestBody List<Integer> years){
        return HttpResourceResponse.success(calendarService.formateCalendarFromYl(years));
    }

    @ApiOperation("获取一段时间内交易日工作日假日ttm")
    @PostMapping("/getCalendarProperty")
    public HttpResourceResponse<CalendarProperty> getCalendarProperty(@RequestBody @Valid CalendarPropertyQueryDto calendarQueryDto){
        return HttpResourceResponse.success(calendarService.getCalendarProperty(calendarQueryDto));
    }

    @ApiOperation("批量-获取一段时间内交易日工作日假日ttm")
    @PostMapping("/getCalendarPropertyBatch")
    public HttpResourceResponse<List<CalendarProperty>> getCalendarPropertyBatch(@RequestBody @Valid CalendarPropertyBatchQueryDto dto){
        return HttpResourceResponse.success(calendarService.getCalendarPropertyBatch(dto));
    }

    @ApiOperation("获取一段时间内交易日")
    @PostMapping("/getDateList")
    public HttpResourceResponse<List<LocalDate>> getTradeDateList(@RequestBody @Valid CalendarStartEndDto calendarQueryDto){
        return HttpResourceResponse.success(calendarService.getTradeDateList(calendarQueryDto));
    }

    /**
     * 获取非交易日日历列表
     * @return 非交易日列表
     */
    @ApiOperation("获取非交易日日历列表")
    @GetMapping("/getNotTradeDateList")
    public HttpResourceResponse<NotTradeVO> getNotTradeDateList(){
        NotTradeVO notTradeVO = new NotTradeVO();
        notTradeVO.setNotTradeDayList(calendarService.getNotTradeDateList());
        notTradeVO.setTradeDay(systemConfigUtil.getTradeDay());
        return HttpResourceResponse.success(notTradeVO);
    }
    /**
     * 获取n天后的交易日
     * @param tradayAddDaysDto  交易日请求参数
     * @return 目标交易日
     * @apiNote 该接口是以物理日期为维度的获取交易日信息，切日后会有问题，慎重使用
     */
    @ApiOperation("获取n天后的交易日")
    @PostMapping("/tradayAddDays")
    public HttpResourceResponse<LocalDate> tradayAddDays(@RequestBody @Valid TradayAddDaysDto tradayAddDaysDto){
        return HttpResourceResponse.success(calendarService.tradayAddDays(tradayAddDaysDto));
    }

    @ApiOperation("日历分页查询")
    @PostMapping("/selectListByPage")
    public HttpResourceResponse<IPage<CalendarPageListVo>> selectListByPage(@RequestBody CalendarPageListDto dto){
        return HttpResourceResponse.success(calendarService.selectListByPage(dto));
    }

    @ApiOperation("新增日历")
    @PostMapping("/addCalendar")
    public HttpResourceResponse<String> addCalendar(@RequestBody @Valid List<CalendarAddDto> dtos) throws Exception {
        return HttpResourceResponse.success(calendarService.addCalendar(dtos));
    }

    @ApiOperation("修改日历")
    @PostMapping("/updateCalendar")
    public HttpResourceResponse<String> updateCalendar(@RequestBody @Valid List<CalendarUpdateDto> dtos) throws Exception {
        return HttpResourceResponse.success(calendarService.updateCalendar(dtos));
    }

    @ApiOperation("获取日历详情")
    @PostMapping("/getCalendarDetail")
    public HttpResourceResponse<CalendarVo> getCalendarDetail(@RequestBody @Valid CalendarDetailDto dto) throws Exception {
        return HttpResourceResponse.success(calendarService.getCalendarDetail(dto));
    }



}
