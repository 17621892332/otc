package org.orient.otc.user.service;


import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.user.entity.UserRole;

import java.util.List;

/**
 * @author 潘俊材
 */
public interface UserRoleService extends IServicePlus<UserRole> {
    /**
     * 批量保存角色信息
     * @param roleIdList 角色列表
     * @param userId 用户ID
     */
    void savaUserRole(List<Integer> roleIdList,Integer userId);
}
