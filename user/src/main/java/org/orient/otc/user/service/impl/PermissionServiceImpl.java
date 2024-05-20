package org.orient.otc.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.util.ThreadContext;
import org.orient.otc.user.dto.PermissionAddDto;
import org.orient.otc.user.dto.PermissionPageListDto;
import org.orient.otc.user.dto.PermissionSaveDto;
import org.orient.otc.user.dto.PermissionTreeDto;
import org.orient.otc.user.entity.Permission;
import org.orient.otc.user.mapper.PermissionMapper;
import org.orient.otc.user.service.PermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author dzrh
 */
@Service
@Slf4j
public class PermissionServiceImpl extends ServiceImpl<BaseMapper<Permission>, Permission> implements PermissionService {
    @Autowired
    PermissionService permissionService;

    @Resource
    PermissionMapper permissionMapper;

    @Override
    public Page<Permission> getListBypage(PermissionPageListDto dto) {
        LambdaQueryWrapper<Permission> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        permissionLambdaQueryWrapper.like(!StringUtils.isEmpty(dto.getPermissionName()),Permission::getPermissionName,dto.getPermissionName())
                .eq(!StringUtils.isEmpty(dto.getPermissionCode()),Permission::getPermissionCode,dto.getPermissionCode())
                .eq(!StringUtils.isEmpty(dto.getPermissionType()),Permission::getPermissionType,dto.getPermissionType())
                .eq(!StringUtils.isEmpty(dto.getParentId()),Permission::getParentId,dto.getParentId())
                .eq(!StringUtils.isEmpty(dto.getNotes()),Permission::getNotes,dto.getNotes())
                .eq(!StringUtils.isEmpty(dto.getHidden()),Permission::getHidden,dto.getHidden())
                .eq(!StringUtils.isEmpty(dto.getName()),Permission::getName,dto.getName())
                .eq(!StringUtils.isEmpty(dto.getPath()),Permission::getPath,dto.getPath())
                .eq(!StringUtils.isEmpty(dto.getComponent()),Permission::getComponent,dto.getComponent())
                .eq(!StringUtils.isEmpty(dto.getSort()),Permission::getSort,dto.getSort())
                .eq(!StringUtils.isEmpty(dto.getIsCache()),Permission::getIsCache,dto.getIsCache())
                .eq(!StringUtils.isEmpty(dto.getStatus()),Permission::getStatus,dto.getStatus())
                .eq(Permission::getIsDeleted, IsDeletedEnum.NO)
                ;
        Page<Permission> page =this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),permissionLambdaQueryWrapper);
        return page;
    }

    @Override
    public String addPermission(PermissionAddDto dto) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(dto,permission);
        permission.setIsDeleted(0);
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        permissionService.saveOrUpdate(permission);
        return "permission add success";
    }

    @Override
    public String updatePermission(PermissionSaveDto dto) {
        AuthorizeInfo authorizeInfo = ThreadContext.getAuthorizeInfo();
        LambdaUpdateWrapper<Permission> permissionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        permissionLambdaUpdateWrapper.eq(Permission::getId,dto.getId())
                        .set(Permission::getPermissionCode,dto.getPermissionCode())
                        .set(Permission::getPermissionName,dto.getPermissionName())
                        .set(Permission::getPermissionType,dto.getPermissionType())
                        .set(Permission::getParentId,dto.getParentId())
                        .set(Permission::getNotes,dto.getNotes())
                        .set(Permission::getIcon,dto.getIcon())
                        .set(Permission::getHidden,dto.getHidden())
                        .set(Permission::getName,dto.getName())
                        .set(Permission::getPath,dto.getPath())
                        .set(Permission::getComponent,dto.getComponent())
                        .set(Permission::getSort,dto.getSort())
                        .set(Permission::getIsCache,dto.getIsCache())
                        .set(Permission::getStatus,dto.getStatus())
                        .set(Permission::getUpdateTime,LocalDateTime.now());
        permissionService.update(permissionLambdaUpdateWrapper);
        return "permission update success";
    }

    @Override
    public String deletePermission(Integer id) {
        List<Integer> delteIds = getChildrenIds(id);
        LambdaUpdateWrapper<Permission> permissionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        permissionLambdaUpdateWrapper.in(Permission::getId,delteIds)
                .set(Permission::getIsDeleted,1)
                .set(Permission::getUpdateTime,new Date());
        permissionService.update(permissionLambdaUpdateWrapper);
        return "permission delete success";
    }

    /**
     * 获取当前删除权限的所有子孙权限
     * @param id
     * @return
     */
    public List<Integer> getChildrenIds(Integer id) {
        LambdaQueryWrapper<Permission> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        AtomicReference<List<Permission>> list = new AtomicReference<>(permissionMapper.selectList(permissionLambdaQueryWrapper));
        List<Integer> childrenIds = new ArrayList<>();
        childrenIds.add(id);
        AtomicBoolean hasChildren = new AtomicBoolean(true);
        // 地柜获取所有子孙节点
        while(hasChildren.get()) {
            hasChildren.set(false);
            ListIterator<Integer> iterator = childrenIds.listIterator();
            while(iterator.hasNext()) {
                Integer itemId = iterator.next();
                list.set(list.get().stream().filter(item -> {
                    if (item.getParentId() == itemId) {
                        iterator.add(item.getId());
                        hasChildren.set(true);
                        return false;
                    } else {
                        return true;
                    }
                }).collect(Collectors.toList()));
            }
        }
        return childrenIds;
    }

    @Override
    public List<PermissionTreeDto> treePermission() {
        LambdaQueryWrapper<Permission> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询非删除权限 , 和非禁用权限
        permissionLambdaQueryWrapper.eq(Permission::getIsDeleted,0) // 未删除
                        .eq(Permission::getStatus,1); // 正常
        List<Permission> listAll = permissionMapper.selectList(permissionLambdaQueryWrapper);
        List<PermissionTreeDto> list = buildPermissionTree(listAll, 0);
        return list;
    }

    /**
     * 构建权限树结构
     * @param listAll
     * @param parentId
     * @return
     */
    public List<PermissionTreeDto> buildPermissionTree(List<Permission> listAll,Integer parentId) {
        // 本次父节点在parentIds中的元素
        List<PermissionTreeDto> list = new ArrayList<>();
        if (StringUtils.isEmpty(listAll)) {
            return list;
        }
        // 过滤并添加当前节点的子节点
        listAll = listAll.stream().filter(item->{
            if(parentId == item.getParentId()){
                PermissionTreeDto dto = new PermissionTreeDto();
                BeanUtils.copyProperties(item,dto);
                list.add(dto);
                return false;
            } else {
                return true;
            }
        }).collect(Collectors.toList());
        for (PermissionTreeDto item : list) {
            List<PermissionTreeDto> childrenList = buildPermissionTree(listAll,item.getId());
            item.setChildrenList(childrenList);
        }
        return list;
    }

}
