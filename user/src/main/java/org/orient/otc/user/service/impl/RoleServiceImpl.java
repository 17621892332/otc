package org.orient.otc.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.user.dto.RoleDeletDto;
import org.orient.otc.user.dto.RoleDetailDto;
import org.orient.otc.user.dto.RolePageListDto;
import org.orient.otc.user.dto.RoleSaveDto;
import org.orient.otc.user.entity.Role;
import org.orient.otc.user.entity.RolePermission;
import org.orient.otc.user.mapper.RoleMapper;
import org.orient.otc.user.mapper.RolePermissionMapper;
import org.orient.otc.user.mapper.UserMapper;
import org.orient.otc.user.service.RoleService;
import org.orient.otc.user.vo.RoleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dzrh
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    RolePermissionMapper rolePermissionMapper;

    @Resource
    UserMapper userMapper;

    @Override
    public List<RoleVo> getRoleList() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getIsDeleted,0);
        return this.listVo(queryWrapper,RoleVo.class);
    }

    @Override
    @Transactional
    public Boolean saveRole(RoleSaveDto roleSaveDto) {
        Role role = BeanUtil.toBean(roleSaveDto, Role.class);
        boolean flag = this.saveOrUpdate(role);
        // 主键非空 , 是修改操作
        if(!StringUtils.isEmpty(roleSaveDto.getId())) {
            Integer roleId  = roleSaveDto.getId();
            LambdaUpdateWrapper<RolePermission> rolePermissionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            rolePermissionLambdaUpdateWrapper.eq(RolePermission::getRoleId,roleId)
                    .set(RolePermission::getIsDeleted,1);
            rolePermissionMapper.update(null,rolePermissionLambdaUpdateWrapper);
        }

        if (flag){
            Integer roleId = role.getId();
            List<Integer> permissionIds  = roleSaveDto.getPermissionIds();
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            permissionIds.forEach(pid -> {
                rp.setId(null);
                rp.setPermissionId(pid);
                rolePermissionMapper.insert(rp);
            });
        }
        return flag;
    }

    @Override
    public IPage<RoleVo> getListByPage(RolePageListDto dto) {
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!StringUtils.isEmpty(dto.getRoleName()),Role::getRoleName,dto.getRoleName())
                .like(!StringUtils.isEmpty(dto.getNotes()),Role::getNotes,dto.getNotes())
                .eq(Role::getIsDeleted,0)
        ;
        IPage<Role> page = this.page(new Page(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        IPage<RoleVo> returnPage = page.convert(item->{
            RoleVo roleVo = new RoleVo();
            BeanUtils.copyProperties(item,roleVo);
            // 获取角色的权限
            LambdaQueryWrapper<RolePermission> rolePermissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            rolePermissionLambdaQueryWrapper.eq(RolePermission::getRoleId,item.getId())
                    .eq(RolePermission::getIsDeleted,0)        ;
            List<RolePermission> rolePermissionList = rolePermissionMapper.selectList(rolePermissionLambdaQueryWrapper);
            if (null != rolePermissionList ){
                List<Integer> rolePermissionIds = new ArrayList<>();
                for (RolePermission rp : rolePermissionList){
                    rolePermissionIds.add(rp.getPermissionId());
                }
                roleVo.setPermissionIds(rolePermissionIds);
            }
            return roleVo;
        });
        return returnPage;
    }

    @Override
    public HttpResourceResponse deleteRole(RoleDeletDto dto) {
        int count = userMapper.countUserByRoleId(dto.getId());
        if (count > 0) {
            return HttpResourceResponse.error(-1,"当前角色不能删除,存在活跃用户持有当前角色");
        }
        LambdaUpdateWrapper<Role> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Role::getId,dto.getId())
                .set(Role::getIsDeleted,1)
                .set(Role::getUpdateTime,LocalDateTime.now())
        ;
        this.update(lambdaUpdateWrapper);
        return HttpResourceResponse.success("role delete success");
    }

    @Override
    public RoleVo getRoleDetail(RoleDetailDto dto) {
        RoleVo roleVo = new RoleVo();
        Role role = this.getById(dto.getId());
        BeanUtils.copyProperties(role,roleVo);
        LambdaQueryWrapper<RolePermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RolePermission::getRoleId,role.getId())
                .eq(RolePermission::getIsDeleted,0)        ;
        List<RolePermission> rolePermissionList = rolePermissionMapper.selectList(lambdaQueryWrapper);
        if (null != rolePermissionList ){
            List<Integer> rolePermissionIds = new ArrayList<>();
            for (RolePermission rp : rolePermissionList){
                rolePermissionIds.add(rp.getPermissionId());
            }
            roleVo.setPermissionIds(rolePermissionIds);
        }
        return roleVo;
    }
}
