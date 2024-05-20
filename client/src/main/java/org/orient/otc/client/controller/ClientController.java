package org.orient.otc.client.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.client.dto.AffiliatedOrganizationDto;
import org.orient.otc.client.dto.ClientDetailDto;
import org.orient.otc.client.dto.ClientPageDto;
import org.orient.otc.client.entity.Client;
import org.orient.otc.client.service.ClientService;
import org.orient.otc.client.vo.AffiliatedOrganizationVo;
import org.orient.otc.client.vo.ClientDetailVo;
import org.orient.otc.client.vo.ClientVo;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author pjc
 */
@RestController
@RequestMapping("/client")
@Api(tags = "客户")
public class ClientController {
    @Autowired
    private ClientService clientService;
    @PostMapping("/list")
    @ApiOperation("获取客户列表")
    @CheckPermission
    public HttpResourceResponse<List<Client>> getList(){
        return HttpResourceResponse.success(clientService.getList());
    }

    @PostMapping("/getListByPage")
    @ApiOperation("客户列表分页查询")
    @CheckPermission
    public HttpResourceResponse<IPage<ClientVo>> getListByPage(@RequestBody ClientPageDto dto){
        return HttpResourceResponse.success(clientService.getListByPage(dto));
    }

    @PostMapping("/getClientAndBankInfoList")
    @ApiOperation("客户列表查询(包含每个客户的银行账户信息)")
    @CheckPermission
    public HttpResourceResponse<List<ClientVo>> getClientAndBankInfoList(){
        return HttpResourceResponse.success(clientService.getClientAndBankInfoList());
    }

    @GetMapping("/getClientDetail")
    @ApiOperation("客户详情查询")
    @CheckPermission
    public HttpResourceResponse<ClientDetailVo> getClientDetail(@RequestParam(value ="clientCode") @Valid String clientCode){
        return HttpResourceResponse.success(clientService.getClientDetail(clientCode));
    }
    @PostMapping("/add")
    @ApiOperation("客户详情新增")
    @CheckPermission
    public HttpResourceResponse<String> add(@RequestBody ClientDetailDto clientDetailDto){
        return HttpResourceResponse.success(clientService.add(clientDetailDto));
    }
    @PostMapping("/update")
    @ApiOperation("客户详情修改")
    @CheckPermission
    public HttpResourceResponse<String> update(@RequestBody ClientDetailDto clientDetailDto){
        return HttpResourceResponse.success(clientService.update(clientDetailDto));
    }
    @GetMapping("/delete")
    @ApiOperation("客户详情删除")
    @CheckPermission
    public HttpResourceResponse<String> delete(@RequestParam(value ="clientCode") @Valid String clientCode){
        return HttpResourceResponse.success(clientService.delete(clientCode));
    }
    @PostMapping("/getAffiliatedOrganization")
    @ApiOperation("所属机构查询")
    @CheckPermission
    public HttpResourceResponse<List<AffiliatedOrganizationVo>> getAffiliatedOrganization(@RequestBody AffiliatedOrganizationDto dto){
        return HttpResourceResponse.success(clientService.getAffiliatedOrganization(dto));
    }
}
