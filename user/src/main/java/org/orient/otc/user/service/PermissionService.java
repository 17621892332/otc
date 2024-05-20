package org.orient.otc.user.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.user.dto.PermissionAddDto;
import org.orient.otc.user.dto.PermissionPageListDto;
import org.orient.otc.user.dto.PermissionSaveDto;
import org.orient.otc.user.dto.PermissionTreeDto;
import org.orient.otc.user.entity.Permission;

import java.util.List;

/**
 * @author 潘俊材
 */
public interface PermissionService extends IServicePlus<Permission> {

    Page<Permission> getListBypage(PermissionPageListDto permissionPageListDto);

    String addPermission(PermissionAddDto permissionAddDto);

    String updatePermission(PermissionSaveDto permissionSaveDto);

    String deletePermission(Integer id);

    List<PermissionTreeDto> treePermission();
}
