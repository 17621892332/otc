package org.orient.otc.client.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.client.dto.clientlevel.ClientLevelAddDto;
import org.orient.otc.client.dto.clientlevel.ClientLevelDeleteDto;
import org.orient.otc.client.dto.clientlevel.ClientLevelListDto;
import org.orient.otc.client.dto.clientlevel.ClientLevelUpdateDto;
import org.orient.otc.client.entity.ClientLevel;
import org.orient.otc.client.service.ClientLevelService;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/client/level")
@Api(tags = "客户等级", description = "客户等级接口")
public class ClientLevelController {
    @Autowired
    private ClientLevelService clientLevelService;
    @PostMapping("/getClientLevelById")
    @ApiOperation("获取客户等级详情")
    public HttpResourceResponse<ClientLevel> getClientLevelById(@RequestParam Integer id){
        return HttpResourceResponse.success(clientLevelService.getClientLevelById(id));
    }
    @PostMapping("/getList")
    @ApiOperation("获取所有客户等级")
    public HttpResourceResponse<List<ClientLevel>> getList(@RequestBody ClientLevelListDto dto){
        return HttpResourceResponse.success(clientLevelService.getList(dto));
    }

    @PostMapping("/update")
    @ApiOperation("更新客户等级")
    public HttpResourceResponse<String> updateClientLevel(@RequestBody ClientLevelUpdateDto dto){
        return HttpResourceResponse.success(clientLevelService.updateClientLevel(dto));
    }

    @PostMapping("/add")
    @ApiOperation("新增客户等级")
    public HttpResourceResponse<String> addClientLevel(@RequestBody @Valid ClientLevelAddDto dto){
        return HttpResourceResponse.success(clientLevelService.addClientLevel(dto));
    }
    @PostMapping("/delete")
    @ApiOperation("删除客户等级")
    public HttpResourceResponse<String> deleteClientLevel(@RequestBody @Valid ClientLevelDeleteDto dto){
        return HttpResourceResponse.success(clientLevelService.deleteClientLevel(dto.getId()));
    }

}
