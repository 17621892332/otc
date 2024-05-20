package org.orient.otc.quote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.riskearlywaring.RiskEarlyWarningAddDto;
import org.orient.otc.quote.dto.riskearlywaring.RiskEarlyWarningPageDto;
import org.orient.otc.quote.service.RiskEarlyWarningService;
import org.orient.otc.quote.vo.riskearlywaring.RiskEarlyWarningVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/riskEarlyWarning")
@Api(tags = "风险预警", description = "风险预警")
@Slf4j
public class RiskEarlyWarningController {
    @Autowired
    RiskEarlyWarningService riskEarlyWarningService;

    @PostMapping("/selectListByPage")
    @ApiOperation("风险预警分页查询")
    public HttpResourceResponse<IPage<RiskEarlyWarningVO>> selectListByPage(@RequestBody RiskEarlyWarningPageDto dto){
        return HttpResourceResponse.success(riskEarlyWarningService.selectListByPage(dto));
    }
    @PostMapping("/add")
    @ApiOperation("风险预警新增")
    public HttpResourceResponse<String> add(@RequestBody RiskEarlyWarningAddDto dto){
        return HttpResourceResponse.success(riskEarlyWarningService.add(dto));
    }
}
