package org.orient.otc.user.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.user.entity.UserRole;
import org.orient.otc.user.mapper.UserRoleMapper;
import org.orient.otc.user.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dzrh
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {


    @Override
    public void savaUserRole(List<Integer> roleIdList,Integer userId) {
        LambdaUpdateWrapper<UserRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserRole::getUserId,userId)
                .set(UserRole::getIsDeleted,1)
        ;
        this.update(updateWrapper);
        List<UserRole> list = CglibUtil.copyList(roleIdList,UserRole::new,(obj,tar)->{
            tar.setRoleId(obj);
            tar.setUserId(userId);
        });
        this.saveBatch(list);
    }
}
