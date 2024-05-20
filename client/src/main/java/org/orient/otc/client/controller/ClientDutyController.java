package org.orient.otc.client.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.client.dto.ClientDutyDto;
import org.orient.otc.client.service.ClientDutyService;
import org.orient.otc.client.vo.ClientDutyVo;
import org.orient.otc.client.vo.ClientMailVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientDuty")
@Api(tags = "人员信息", description = "人员信息接口")
public class ClientDutyController {
    @Autowired
    private ClientDutyService clientDutyService;
    @GetMapping("/list")
    @ApiOperation("人员信息列表查询")
    @CheckPermission
    public HttpResourceResponse<List<ClientDutyVo>> list(@RequestParam(value ="id") @Valid String id){
        return HttpResourceResponse.success(clientDutyService.list(id));
    }
    @PostMapping("/add")
    @ApiOperation("人员信息新增")
    @CheckPermission
    public HttpResourceResponse<String> add(@RequestBody @Valid ClientDutyDto clientDutyDto){
        return HttpResourceResponse.success(clientDutyService.add(clientDutyDto));
    }
    @PostMapping("/update")
    @ApiOperation("人员信息修改")
    @CheckPermission
    public HttpResourceResponse<String> update(@RequestBody @Valid ClientDutyDto clientDutyDto){
        return HttpResourceResponse.success(clientDutyService.update(clientDutyDto));
    }
    @PostMapping("/delete")
    @ApiOperation("人员信息删除")
    @CheckPermission
    public HttpResourceResponse<String> delete(@RequestBody @Valid ClientDutyDto clientDutyDto){
        return HttpResourceResponse.success(clientDutyService.delete(clientDutyDto));
    }

    /**
     * 获取某个客户的人员信息列表
     * 返回key = 联系人类型, value=联系人邮箱列表
     * @param id 客户id
     * @return 返回map
     */
    @GetMapping("/getMapByClientId")
    @ApiOperation("获取某个客户的人员信息列表")
    @CheckPermission
    public HttpResourceResponse<Map<String, List<ClientMailVO>>> getMapByClientId(@RequestParam(value ="id") @Valid String id){
        return HttpResourceResponse.success(clientDutyService.getMapByClientId(id));
    }
}
