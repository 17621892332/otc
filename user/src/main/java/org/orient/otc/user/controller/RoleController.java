package org.orient.otc.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.user.dto.RoleDeletDto;
import org.orient.otc.user.dto.RoleDetailDto;
import org.orient.otc.user.dto.RoleSaveDto;
import org.orient.otc.user.dto.RolePageListDto;
import org.orient.otc.user.service.RoleService;
import org.orient.otc.user.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 潘俊材
 */
@RestController
@RequestMapping("/role")
@Api(tags = "角色管理")
public class RoleController {

    @Autowired
    RoleService roleService;

    /**
     * 获取角色列表
     * @return 角色列表
     */
    @CheckPermission("user::role::getRoleList")
    @PostMapping("getRoleList")
    public HttpResourceResponse<List<RoleVo>> getRoleList() {
        return HttpResourceResponse.success(roleService.getRoleList());
    }

    /**
     * 保存角色信息
     * @param roleSaveDto 角色对象 {@link RoleSaveDto}
     * @return 是否保存成功 true 成功 false 失败
     *
     * @apiNote 用于新增或者编辑角色信息，新增时ID应为空
     */
    @ApiOperation(value = "保存角色信息", notes = "用于新增或者编辑角色信息，新增时ID应为空")
    @CheckPermission("user::role::saveRole")
    @PostMapping("saveRole")
    public HttpResourceResponse<Boolean> saveRole(@RequestBody @Valid RoleSaveDto roleSaveDto) {
        return HttpResourceResponse.success(roleService.saveRole(roleSaveDto));
    }
    @ApiModelProperty(value = "角色分页查询", notes = "角色分页查询")
    @CheckPermission("user::role::getListByPage")
    @PostMapping("/getListByPage")
    public HttpResourceResponse<IPage<RoleVo>> getListByPage(@RequestBody @Valid RolePageListDto dto) {
        return HttpResourceResponse.success(roleService.getListByPage(dto));
    }
    @ApiOperation(value = "删除角色信息", notes = "删除角色信息")
    @CheckPermission("user::role::deleteRole")
    @PostMapping("/deleteRole")
    public HttpResourceResponse deleteRole(@RequestBody @Valid RoleDeletDto roleDeletDto) {
        return roleService.deleteRole(roleDeletDto);
    }
    @ApiOperation(value = "获取角色信息", notes = "获取角色信息")
    @CheckPermission("user::role::getRoleDetail")
    @PostMapping("/getRoleDetail")
    public HttpResourceResponse<RoleVo> getRoleDetail(@RequestBody @Valid RoleDetailDto roleGetDto) {
        return HttpResourceResponse.success(roleService.getRoleDetail(roleGetDto));
    }
}
