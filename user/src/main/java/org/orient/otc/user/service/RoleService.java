package org.orient.otc.user.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.user.dto.RoleDeletDto;
import org.orient.otc.user.dto.RoleDetailDto;
import org.orient.otc.user.dto.RoleSaveDto;
import org.orient.otc.user.dto.RolePageListDto;
import org.orient.otc.user.entity.Role;
import org.orient.otc.user.vo.RoleVo;

import java.util.List;

/**
 * @author 潘俊材
 */
public interface RoleService extends IServicePlus<Role> {

    /**
     * 获取角色列表
     * @return 角色列表
     */
    List<RoleVo> getRoleList();

    /**
     * 保存角色信息
     * @param roleSaveDto 请求对象
     * @return 是否成功
     */
    Boolean saveRole(RoleSaveDto roleSaveDto);

    /**
     * 获取角色分页信息
     * @param dto 请求对象
     * @return 角色分页对象
     */
    IPage<RoleVo> getListByPage(RolePageListDto dto);

    /**
     * 删除角色信息
     * @param roleDeletDto 请求对象
     * @return 返回信息
     */
    HttpResourceResponse deleteRole(RoleDeletDto roleDeletDto);

    /**
     * 获取角色详情
     * @param roleGetDto 请求参数
     * @return 角色对象
     */
    RoleVo getRoleDetail(RoleDetailDto roleGetDto);
}
