package org.orient.otc.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.common.core.util.ObjectEqualsUtil;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.system.dto.clientdatachangerecord.ClientDataChangeRecordDetailDto;
import org.orient.otc.system.dto.clientdatachangerecord.ClientDataChangeRecordPageDto;
import org.orient.otc.system.service.ClientDataChangeRecordService;
import org.orient.otc.system.vo.ClientDataChangeRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientDataChangeRecord")
@Api(tags = "客户数据变更")
public class ClientDataChangeRecordController {
    @Autowired
    ClientDataChangeRecordService clientDataChangeRecordService;
    @Autowired
    ObjectEqualsUtil objectEqualsUtil;

    @Autowired
    ClientClient clientClient;

    @PostMapping("/selectByPage")
    @ApiOperation("分页查询")
    public HttpResourceResponse<IPage<ClientDataChangeRecordVO>> selectByPage(@RequestBody ClientDataChangeRecordPageDto dto){
        return HttpResourceResponse.success(clientDataChangeRecordService.selectByPage(dto));
    }

    @PostMapping("/getDetails")
    @ApiOperation("查看详情")
    public HttpResourceResponse<ClientDataChangeRecordVO> getDetails(@RequestBody ClientDataChangeRecordDetailDto dto){
        return HttpResourceResponse.success(clientDataChangeRecordService.getDetails(dto));
    }
}
