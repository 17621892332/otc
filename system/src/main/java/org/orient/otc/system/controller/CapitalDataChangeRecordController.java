package org.orient.otc.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.util.ObjectEqualsUtil;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.system.dto.capitaldatachangerecord.CapitalDataChangeRecordPageDto;
import org.orient.otc.system.service.CapitalDataChangeRecordService;
import org.orient.otc.system.vo.CapitalDataChangeRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/capitalDataChangeRecord")
@Api(tags = "资金数据变更记录")
public class CapitalDataChangeRecordController {
    @Autowired
    CapitalDataChangeRecordService capitalDataChangeRecordService;
    @Autowired
    ObjectEqualsUtil objectEqualsUtil;

    @PostMapping("/selectByPage")
    @ApiOperation("分页查询")
    public HttpResourceResponse<IPage<CapitalDataChangeRecordVO>> selectByPage(@RequestBody CapitalDataChangeRecordPageDto dto){
        return HttpResourceResponse.success(capitalDataChangeRecordService.selectByPage(dto));
    }
}
