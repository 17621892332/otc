package org.orient.otc.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.Assetunit;
import org.orient.otc.user.service.AssetunitService;
import org.orient.otc.user.vo.AssetunitVo;
import org.orient.otc.user.vo.TraderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/assetunit")
@Api(tags = "簿记账户", description = "簿记账户")
@ApiResponses({@ApiResponse(code = 500, message = "服务器内部错误", response = HttpResourceResponse.class)})
public class AssetunitController {
    @Autowired
    private AssetunitService assetunitService;

    /**
     * 获取簿记账户列表
     */
    @ApiOperation("获取簿记账户列表")
    @PostMapping("/list")
    @CheckPermission("user::assetunit::list")
    public HttpResourceResponse<List<Assetunit>> getList(){
        return HttpResourceResponse.success(assetunitService.getList());
    }

    /**
     * 获取交易员列表
     */
    @ApiOperation("获取交易员列表")
    @PostMapping("/trader/list")
    @CheckPermission("user::assetunit::trader/list")
    public  HttpResourceResponse<List<TraderVo>> getTraderList(){
        return HttpResourceResponse.success(assetunitService.getTraderList());
    }

    /**
     * 查询簿记账户分页列表
     */
    @ApiOperation("查询簿记账户分页列表")
    @PostMapping("/getListByPage")
    @CheckPermission("user::assetunit::getListByPage")
    public HttpResourceResponse<IPage<AssetunitVo>> getListByPage(@RequestBody AssetunitPageListDto dto){
        return HttpResourceResponse.success(assetunitService.getListByPage(dto));
    }
    /**
     * 新增簿记账户
     */
    @ApiOperation("新增簿记账户")
    @PostMapping("/addAssetunit")
    @CheckPermission("user::assetunit::addAssetunit")
    public HttpResourceResponse<String> addAssetunit(@RequestBody AssetunitAddDto dto){
        return HttpResourceResponse.success(assetunitService.addAssetunit(dto));
    }
    /**
     * 修改簿记账户
     */
    @ApiOperation("修改簿记账户")
    @PostMapping("/updateAssetunit")
    @CheckPermission("user::assetunit::updateAssetunit")
    public HttpResourceResponse<String> updateAssetunit(@RequestBody @Valid AssetunitUpdateDto dto){
        return HttpResourceResponse.success(assetunitService.updateAssetunit(dto));
    }
    /**
     * 修改簿记账户
     */
    @ApiOperation("删除簿记账户")
    @PostMapping("/deleteAssetunit")
    @CheckPermission("user::assetunit::deleteAssetunit")
    public HttpResourceResponse<String> deleteAssetunit(@RequestBody @Valid AssetunitDeleteDto dto){
        return HttpResourceResponse.success(assetunitService.deleteAssetunit(dto));
    }
    /**
     * 修改簿记账户
     */
    @ApiOperation("获取簿记账户详情")
    @PostMapping("/getAssetunitDetail")
    @CheckPermission("user::assetunit::getAssetunitDetail")
    public HttpResourceResponse<AssetunitVo> getAssetunitDetail(@RequestBody @Valid AssetunitDetailDto dto){
        return HttpResourceResponse.success(assetunitService.getAssetunitDetail(dto));
    }
}
