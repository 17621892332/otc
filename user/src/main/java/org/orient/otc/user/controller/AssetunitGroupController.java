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
import org.orient.otc.user.entity.AssetunitGroup;
import org.orient.otc.user.service.AssetunitGroupService;
import org.orient.otc.user.vo.AssetunitGroupVo;
import org.orient.otc.user.vo.TraderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/assetunitGroup")
@Api(tags = "簿记账户组", description = "簿记账户组")
@ApiResponses({@ApiResponse(code = 500, message = "服务器内部错误", response = HttpResourceResponse.class)})
public class AssetunitGroupController {
    @Autowired
    private AssetunitGroupService assetunitGroupService;

    /**
     * 获取未删除的簿记账户列表
     */
    @ApiOperation("获取簿记账户组列表")
    @PostMapping("/list")
    @CheckPermission("user::assetunitGroup::list")
    public HttpResourceResponse<List<AssetunitGroup>> getList(){
        return HttpResourceResponse.success(assetunitGroupService.getList());
    }

    /**
     * 查询的簿记账户分页列表
     */
    @ApiOperation("查询簿记账户组分页列表")
    @PostMapping("/getListBypage")
    @CheckPermission("user::assetunitGroup::getListBypage")
    public HttpResourceResponse<IPage<AssetunitGroupVo>> getListBypage(@RequestBody AssetunitGroupPageListDto dto){
        return HttpResourceResponse.success(assetunitGroupService.getListBypage(dto));
    }

    /**
     * 获取薄记账户组详情
     */
    @ApiOperation("获取薄记账户组详情")
    @PostMapping("/getAssetunitGroupDetail")
    @CheckPermission("user::assetunitGroup::getAssetunitGroupDetail")
    public HttpResourceResponse<AssetunitGroup> getAssetunitGroupDetail(@RequestBody @Valid AssetunitGroupDetailDto dto){
        return HttpResourceResponse.success(assetunitGroupService.getAssetunitGroupDetail(dto));
    }

    /**
     * 新增薄记账户
     */
    @ApiOperation("新增薄记账户")
    @PostMapping("/addAssetunitGroup")
    @CheckPermission("user::assetunitGroup::addAssetunitGroup")
    public HttpResourceResponse<String> addAssetunitGroup(@RequestBody AssetunitGroupAddDto dto){
        return HttpResourceResponse.success(assetunitGroupService.addAssetunitGroup(dto));
    }

    /**
     * 修改薄记账户
     */
    @ApiOperation("修改薄记账户")
    @PostMapping("/updateAssetunitGroup")
    @CheckPermission("user::assetunitGroup::updateAssetunitGroup")
    public HttpResourceResponse<String> updateAssetunitGroup(@RequestBody @Valid AssetunitGroupUpdateDto dto){
        return HttpResourceResponse.success(assetunitGroupService.updateAssetunitGroup(dto));
    }

    /**
     * 修改薄记账户
     */
    @ApiOperation("删除薄记账户")
    @PostMapping("/deleteAssetunitGroup")
    @CheckPermission("user::assetunitGroup::deleteAssetunitGroup")
    public HttpResourceResponse<String> deleteAssetunitGroup(@RequestBody @Valid AssetunitGroupDeleteDto dto){
        return HttpResourceResponse.success(assetunitGroupService.deleteAssetunitGroup(dto));
    }



}
