package org.orient.otc.user.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.Assetunit;
import org.orient.otc.user.entity.AssetunitGroup;
import org.orient.otc.user.entity.AssetunitUser;
import org.orient.otc.user.entity.User;
import org.orient.otc.user.mapper.AssetunitGroupMapper;
import org.orient.otc.user.mapper.AssetunitMapper;
import org.orient.otc.user.mapper.AssetunitUserMapper;
import org.orient.otc.user.mapper.UserMapper;
import org.orient.otc.user.service.AssetunitService;
import org.orient.otc.user.vo.AssetUnitUserVo;
import org.orient.otc.user.vo.AssetunitVo;
import org.orient.otc.user.vo.TraderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 簿记账户实现
 */
@Service
@Slf4j
public class AssetunitServiceImpl extends ServiceImpl<BaseMapper<Assetunit>, Assetunit> implements AssetunitService {

    @Resource
    private AssetunitMapper assetunitMapper;
    @Resource
    private AssetunitUserMapper assetunitUserMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private AssetunitGroupMapper assetunitGroupMapper;

    @Override
    public List<Assetunit> getList() {
        return assetunitMapper.selectList(new LambdaQueryWrapper<Assetunit>().eq(Assetunit :: getIsDeleted,0));
    }

    @Override
    public List<TraderVo> getTraderList() {
        LambdaQueryWrapper<AssetunitUser> assetunitUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        assetunitUserLambdaQueryWrapper.eq(AssetunitUser::getIsDeleted,IsDeletedEnum.NO);
        List<AssetunitUser> assetunitUserList = assetunitUserMapper.selectList(assetunitUserLambdaQueryWrapper);

        Map<Integer,List<Integer>> assetMap =assetunitUserList.stream().collect(Collectors.groupingBy(AssetunitUser::getTraderId
                ,Collectors.mapping(AssetunitUser::getAssetunitId,Collectors.toList())));
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().select(User::getId, User::getName)
                .in(User::getId, assetunitUserList.stream().map(AssetunitUser::getTraderId).distinct().collect(Collectors.toList()))
                .eq(User::getIsDeleted,IsDeletedEnum.NO));
        return CglibUtil.copyList(users,TraderVo::new,(s,t)-> t.setAssetunitList(assetMap.get(s.getId())));
    }


    @Override
    public List<Assetunit> queryByIds(Set<Integer> ids) {
        LambdaQueryWrapper<Assetunit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Assetunit::getId,Assetunit::getName);
        queryWrapper.eq(Assetunit::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.in(ids!=null&& !ids.isEmpty(),Assetunit::getId,ids );
        return this.list(queryWrapper);
    }

    @Override
    public IPage<AssetunitVo> getListByPage(AssetunitPageListDto dto) {
        // 交易员
        List<AssetunitUser> assetunitUserList;
        List<Integer> assetUnitIds = null;
        if (null != dto.getTraderIds() && !dto.getTraderIds().isEmpty()) {
            LambdaQueryWrapper<AssetunitUser> assetunitUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            assetunitUserLambdaQueryWrapper.in(AssetunitUser::getTraderId,dto.getTraderIds());
            assetunitUserLambdaQueryWrapper.eq(AssetunitUser::getIsDeleted,IsDeletedEnum.NO);
            assetunitUserList = assetunitUserMapper.selectList(assetunitUserLambdaQueryWrapper);
            if (null != assetunitUserList && !assetunitUserList.isEmpty()) {
                assetUnitIds = assetunitUserList.stream().map(AssetunitUser::getAssetunitId).collect(Collectors.toList());
            }
        }
        LambdaQueryWrapper<Assetunit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!StringUtils.isEmpty(dto.getName()),Assetunit::getName,dto.getName())
                .eq(!StringUtils.isEmpty(dto.getGroupId()),Assetunit::getGroupId,dto.getGroupId())
                .eq(!StringUtils.isEmpty(dto.getBaseCurrency()),Assetunit::getBaseCurrency,dto.getBaseCurrency())
                .eq(Assetunit::getIsDeleted,IsDeletedEnum.NO)
                .in(null != assetUnitIds && !assetUnitIds.isEmpty(),Assetunit::getId,assetUnitIds);
        // 分页查询簿记账户信息
        IPage<Assetunit> ipage =  this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);

        // 簿记账户对应的交易员
        Map<Integer,List<AssetUnitUserVo>> assetUniteUserMap = getPageResultAssetunitUserMap(ipage);
        // 簿记账户对应的账户组
        Map<Integer,AssetunitGroup> assetUniteGroupmap = getPageResultAssetUniteGroupMap(ipage);
        // 簿记账户的创建人员信息
        Map<Integer,User> userMap = getPageResultUserMap(ipage);
        return ipage.convert(item->{
            AssetunitVo vo = new AssetunitVo();
            BeanUtils.copyProperties(item,vo);
            vo.setAssetunitGroup(assetUniteGroupmap.get(item.getGroupId()));
            vo.setAssetunitUserList(assetUniteUserMap.get(item.getId()));
            vo.setUser(userMap.get(item.getCreatorId()));
            return vo;
        });
    }

    /**
     * 获取簿记账户分页返回结果中对应的交易员
     * @param ipage 查询条件
     * @return key 簿记账户ID value 交易员列表
     */
    public Map<Integer,List<AssetUnitUserVo>> getPageResultAssetunitUserMap(IPage<Assetunit> ipage) {
        // 分页查询结果中的簿记账户ID集合
        List<Integer> pageReturnAssetUnitIds = ipage.getRecords().stream().map(Assetunit::getId).collect(Collectors.toList());
        // 去重
        pageReturnAssetUnitIds = pageReturnAssetUnitIds.stream().distinct().collect(Collectors.toList());
        // 查询簿记账户对应的交易员
        List<AssetUnitUserVo> pageResultAssetunitUserList = userMapper.selectListByAssetUnitIds(pageReturnAssetUnitIds);
        // 根据簿记账户ID分组,得到每个簿记账户对应的交易员集合 ke=簿记账户ID value=交易员list
        return pageResultAssetunitUserList.stream().collect(Collectors.groupingBy(AssetUnitUserVo::getAssetunitId));
    }

    /**
     * 获取簿记账户分页查询返回结果中对应的簿记账户组
     * @param ipage 查询条件
     * @return key 簿记账户组ID value 簿记账户组
     */
    public Map<Integer,AssetunitGroup> getPageResultAssetUniteGroupMap(IPage<Assetunit> ipage){
        // 分页查询结果中的簿记账户组ID集合
        List<Integer> pageResultAssetunitGroupIds = ipage.getRecords().stream().map(Assetunit::getGroupId).collect(Collectors.toList());
        // 去重
        pageResultAssetunitGroupIds = pageResultAssetunitGroupIds.stream().distinct().collect(Collectors.toList());
        LambdaQueryWrapper<AssetunitGroup> assetunitGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
        assetunitGroupLambdaQueryWrapper.in(AssetunitGroup::getId,pageResultAssetunitGroupIds)
                .eq(AssetunitGroup::getIsDeleted,IsDeletedEnum.NO);
        // 查询簿记账户对应的簿记账户组信息
        List<AssetunitGroup> assetunitGroupList = assetunitGroupMapper.selectList(assetunitGroupLambdaQueryWrapper);

        // 根据簿记账户组ID分组,得到每个簿记账户对应的交易员集合 key=账户组id value=簿记账户对象
        return assetunitGroupList.stream().collect(Collectors.toMap(AssetunitGroup::getId, item->item,(v1, v2)->v1));
    }

    /**
     * 获取簿记账户分页查询返回结果中对应的创建人信息
     * @param ipage 查询条件
     * @return key=用户id,value=user对象
     */
    public Map<Integer,User> getPageResultUserMap(IPage<Assetunit> ipage){
        List<Integer> pageResultCreateUserIdlist = ipage.getRecords().stream().map(BaseEntity::getCreatorId).collect(Collectors.toList());
        // 去重
        pageResultCreateUserIdlist = pageResultCreateUserIdlist.stream().distinct().collect(Collectors.toList());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(User::getId,pageResultCreateUserIdlist)
                .eq(User::getIsDeleted,IsDeletedEnum.NO);
        List<User> userList = userMapper.selectList(lambdaQueryWrapper);
        // 根据用户ID分组,key=用户id,value=user对象
        return userList.stream().collect(Collectors.toMap(User::getId, item->item,(v1, v2)->v2));
    }

    @Override
    public String addAssetunit(AssetunitAddDto dto) {
        Assetunit assetunit = new Assetunit();
        BeanUtils.copyProperties(dto,assetunit);
        int count = assetunitMapper.insert(assetunit);
        if (count != 1) {
            return "add failed";
        }
        List<Integer> traderIds = dto.getTraderIds();
        if (null != traderIds && !traderIds.isEmpty()) {
            traderIds.forEach(traderId->{
                AssetunitUser assetunitUser = new AssetunitUser();
                assetunitUser.setAssetunitId(assetunit.getId());
                assetunitUser.setTraderId(traderId);
                assetunitUserMapper.insert(assetunitUser);
            });
        }
        return "add success";
    }

    @Override
    public String updateAssetunit(AssetunitUpdateDto dto) {
        Assetunit assetunit = new Assetunit();
        BeanUtils.copyProperties(dto,assetunit);
        int count = assetunitMapper.updateById(assetunit);
        if (count != 1) {
            return "update failed";
        }
        // 簿记账户和交易员中间表信息,先删后加
        LambdaUpdateWrapper<AssetunitUser> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AssetunitUser::getAssetunitId,dto.getId())
                        .set(AssetunitUser::getIsDeleted,IsDeletedEnum.YES);
        assetunitUserMapper.update(null,lambdaUpdateWrapper);
        // 添加
        List<Integer> traderIds = dto.getTraderIds();
        if (null != traderIds && !traderIds.isEmpty()) {
            traderIds.forEach(traderId->{
                AssetunitUser addAssetunitUser = new AssetunitUser();
                addAssetunitUser.setAssetunitId(assetunit.getId());
                addAssetunitUser.setTraderId(traderId);
                assetunitUserMapper.insert(addAssetunitUser);
            });
        }

        return "update success";
    }

    @Override
    public String deleteAssetunit(AssetunitDeleteDto dto) {
        Assetunit assetunit = new Assetunit();
        BeanUtils.copyProperties(dto,assetunit);
        assetunit.setIsDeleted(IsDeletedEnum.YES.getFlag());
        int count = assetunitMapper.updateById(assetunit);
        if (count != 1) {
            return "delete failed";
        }
        AssetunitUser assetunitUser = new AssetunitUser();
        assetunitUser.setAssetunitId(dto.getId());
        assetunitUser.setIsDeleted(IsDeletedEnum.YES.getFlag());
        assetunitUserMapper.updateById(assetunitUser);
        return "delete success";
    }

    @Override
    public AssetunitVo getAssetunitDetail(AssetunitDetailDto dto) {
        AssetunitVo assetunitVo = new AssetunitVo();
        Assetunit assetunit = assetunitMapper.selectById(dto.getId());
        BeanUtils.copyProperties(assetunit,assetunitVo);

        // 查询簿记账户对应的交易员
        List<AssetUnitUserVo> assetunitUserList = userMapper.selectListByAssetUnitIds(Collections.singletonList(assetunitVo.getId()));
        assetunitVo.setAssetunitUserList(assetunitUserList);

        LambdaQueryWrapper<AssetunitGroup> assetunitGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
        assetunitGroupLambdaQueryWrapper.eq(AssetunitGroup::getId,assetunitVo.getGroupId())
                .eq(AssetunitGroup::getIsDeleted,IsDeletedEnum.NO);
        // 查询簿记账户对应的簿记账户组信息
        List<AssetunitGroup> assetunitGroupList = assetunitGroupMapper.selectList(assetunitGroupLambdaQueryWrapper);
        if(null!=assetunitGroupList && !assetunitGroupList.isEmpty()) {
            assetunitVo.setAssetunitGroup(assetunitGroupList.get(0));
        } else {
            log.info("簿记账户id="+dto.getId()+"未查询到对应的簿记账户组信息");
        }
        return assetunitVo;
    }
}
