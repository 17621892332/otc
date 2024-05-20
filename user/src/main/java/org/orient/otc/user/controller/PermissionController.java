package org.orient.otc.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.Permission;
import org.orient.otc.user.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/permission")
@Api(tags = "权限API",description = "提供权限新增,查询,修改等接口")
public class PermissionController {
    @Autowired
    PermissionService permissionsService;

    @ApiOperation("权限分页查询")
    @PostMapping("/getListBypage")
    @CheckPermission("permission::permission::getListBypage")
    public HttpResourceResponse<Page<Permission>> getListBypage(@RequestBody PermissionPageListDto permissionPageListDto) {
        return HttpResourceResponse.success(permissionsService.getListBypage(permissionPageListDto));
    }
    @ApiOperation("新增权限")
    @PostMapping("/addPermission")
    @CheckPermission("permission::permission::addPermission")
    public HttpResourceResponse<String> addPermission (@RequestBody @Valid PermissionAddDto permissionAddDto) {
        return HttpResourceResponse.success(permissionsService.addPermission(permissionAddDto));
    }

    @ApiOperation("修改权限")
    @PostMapping("/updatePermission")
    @CheckPermission("permission::permission::updatePermission")
    public HttpResourceResponse<String> updatePermission (@RequestBody @Valid PermissionSaveDto permissionSaveDto) {
        return HttpResourceResponse.success(permissionsService.updatePermission(permissionSaveDto));
    }

    @ApiOperation("删除权限")
    @PostMapping("/deletePermission")
    @CheckPermission("permission::permission::deletePermission")
    public HttpResourceResponse<String> deletePermission (@RequestBody PermissionDeleteDto permissionSaveDto) {
        return HttpResourceResponse.success(permissionsService.deletePermission(permissionSaveDto.getId()));
    }

    @ApiOperation("树形权限")
    @PostMapping("/treePermission")
    @CheckPermission("permission::permission::treePermission")
    public HttpResourceResponse<List<PermissionTreeDto>> treePermission () {
        return HttpResourceResponse.success(permissionsService.treePermission());
    }


}
