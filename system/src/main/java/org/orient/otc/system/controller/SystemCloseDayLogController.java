package org.orient.otc.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.system.dto.CloseDayLogDate;
import org.orient.otc.system.dto.CloseDayLogPageDto;
import org.orient.otc.system.dto.SettlementLogDate;
import org.orient.otc.system.dto.SettlementLogPageDto;
import org.orient.otc.system.entity.CloseDayDetailLog;
import org.orient.otc.system.entity.CloseDayLog;
import org.orient.otc.system.entity.SettlementDetailLog;
import org.orient.otc.system.entity.SettlementLog;
import org.orient.otc.system.service.SystemCloseDayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dzrh
 */
@RestController
@RequestMapping("/systemCloseDay")
@Api(tags = "结算切日接口")
public class SystemCloseDayLogController {
    @Autowired
    SystemCloseDayLogService systemCloseDayLogService;
    @PostMapping("/settlement")
    @ApiOperation("结算")
    @CheckPermission("system::system::settlement")
    public HttpResourceResponse<List<SettlementVO>> settlement(@RequestBody SettlementDTO settlementDto){
        return HttpResourceResponse.success(systemCloseDayLogService.settlement(settlementDto));
    }

    @PostMapping("/closeDate")
    @ApiOperation("切日")
    @CheckPermission("system::system::closeDate")
    public HttpResourceResponse<List<SettlementVO>> closeDate(){
        return HttpResourceResponse.success(systemCloseDayLogService.closeDate());
    }
    @PostMapping("/getTodaySettlementLog")
    @ApiOperation("获取结算结果")
    public HttpResourceResponse<List<SettlementDetailLog>>  getTodaySettlementLog(){
        return HttpResourceResponse.success(systemCloseDayLogService.getTodaySettlementLog());
    }

    @PostMapping("/getCloseDayLog")
    @ApiOperation("获取切日结果")
    public HttpResourceResponse<List<CloseDayDetailLog>>  getCloseDayLog(){
        return HttpResourceResponse.success(systemCloseDayLogService.getCloseDayLog());
    }

    @PostMapping("/getCloseDateLogByPage")
    @ApiOperation("分页获取历史切日记录")
    public HttpResourceResponse<Page<CloseDayLog>>  getCloseDateLogByPage(@RequestBody @Valid CloseDayLogPageDto closeDayLogPageDto){
        return HttpResourceResponse.success(systemCloseDayLogService.getCloseDateLogByPage(closeDayLogPageDto));
    }
    @PostMapping("/getSettlementLogByPage")
    @ApiOperation("分页获取历史结算记录")
    public HttpResourceResponse<Page<SettlementLog>>  getSettlementLogByPage(@RequestBody @Valid SettlementLogPageDto settlementLogPageDto){
        return HttpResourceResponse.success(systemCloseDayLogService.getSettlementLogByPage(settlementLogPageDto));
    }

    @PostMapping("/getCloseDateLogDetailByDate")
    @ApiOperation("查询某个日期的切日详情")
    public HttpResourceResponse<List<CloseDayDetailLog>>  getCloseDateLogDetailByDate(@RequestBody @Valid CloseDayLogDate closeDayLogDate){
        return HttpResourceResponse.success(systemCloseDayLogService.getCloseDateLogDetailByDate(closeDayLogDate));
    }

    @PostMapping("/getSettlementLogDetailByDate")
    @ApiOperation("查询某个日期的结算详情")
    public HttpResourceResponse<List<SettlementDetailLog>>  getSettlementLogDetailByDate(@RequestBody @Valid SettlementLogDate settlementLogDate){
        return HttpResourceResponse.success(systemCloseDayLogService.getSettlementLogDetailByDate(settlementLogDate));
    }

    @PostMapping("/clearRedisData")
    @ApiOperation("清空redis计算相关数据")
    public  HttpResourceResponse<SettlementVO> clearRedisData(){
        return HttpResourceResponse.success(systemCloseDayLogService.clearRedisData());
    }
}
