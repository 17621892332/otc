package org.orient.otc.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.Assetunit;
import org.orient.otc.user.entity.AssetunitGroup;
import org.orient.otc.user.entity.ExchangeAccount;
import org.orient.otc.user.entity.User;
import org.orient.otc.user.mapper.AssetunitGroupMapper;
import org.orient.otc.user.mapper.AssetunitMapper;
import org.orient.otc.user.mapper.ExchangeAccountMapper;
import org.orient.otc.user.mapper.UserMapper;
import org.orient.otc.user.service.AssetunitGroupService;
import org.orient.otc.user.vo.AssetunitGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetunitGroupServiceImpl extends ServiceImpl<BaseMapper<AssetunitGroup>, AssetunitGroup> implements AssetunitGroupService {

    @Resource
    AssetunitGroupMapper assetunitGroupMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    ExchangeAccountMapper exchangeAccountMapper;

    @Autowired
    AssetunitMapper assetunitMapper;

    @Override
    public List<AssetunitGroup> getList() {
        LambdaQueryWrapper<AssetunitGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AssetunitGroup::getIsDeleted,IsDeletedEnum.NO);
        return assetunitGroupMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public IPage<AssetunitGroupVo> getListBypage(AssetunitGroupPageListDto dto) {
        LambdaQueryWrapper<AssetunitGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AssetunitGroup::getIsDeleted,IsDeletedEnum.NO.getFlag())
                .like(!StringUtils.isEmpty(dto.getName()),AssetunitGroup::getName,dto.getName())
                .eq(!StringUtils.isEmpty(dto.getCreatorId()),AssetunitGroup::getCreatorId,dto.getCreatorId());
        IPage<AssetunitGroup> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        Map<Integer, User> userMap = getPageResultUserMap(ipage);
        IPage<AssetunitGroupVo> returnIpage = ipage.convert(item->{
            AssetunitGroupVo vo = new AssetunitGroupVo();
            BeanUtils.copyProperties(item,vo);
            vo.setUser(userMap.get(item.getCreatorId()));
            return vo;
        });
        return returnIpage;
    }
    // 获取簿记账户组分页查询返回结果中对应的创建人信息
    public Map<Integer, User> getPageResultUserMap(IPage<AssetunitGroup> ipage){
        List<Integer> pageResultCreateUserIdlist = ipage.getRecords().stream().map(item->item.getCreatorId()).collect(Collectors.toList());
        // 去重
        pageResultCreateUserIdlist = pageResultCreateUserIdlist.stream().distinct().collect(Collectors.toList());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(User::getId,pageResultCreateUserIdlist)
                .eq(User::getIsDeleted,IsDeletedEnum.NO);
        List<User> userList = userMapper.selectList(lambdaQueryWrapper);
        // 根据用户ID分组,key=用户id,value=user对象
        Map<Integer,User> userMap = userList.stream().collect(Collectors.toMap(item->item.getId(),item->item,(v1,v2)->v2));
        return userMap;
    }

    @Override
    public AssetunitGroup getAssetunitGroupDetail(AssetunitGroupDetailDto dto) {
        return assetunitGroupMapper.selectById(dto.getId());
    }

    @Override
    public String addAssetunitGroup(AssetunitGroupAddDto dto) {
        AssetunitGroup assetunitGroup = new AssetunitGroup();
        BeanUtils.copyProperties(dto,assetunitGroup);
        assetunitGroupMapper.insert(assetunitGroup);
        return "add success ";
    }

    @Override
    public String updateAssetunitGroup(AssetunitGroupUpdateDto dto) {
        AssetunitGroup assetunitGroup = new AssetunitGroup();
        BeanUtils.copyProperties(dto,assetunitGroup);
        boolean flag = this.updateById(assetunitGroup);
        if (flag) {
            return "update success";
        }
        return  "update failed";
    }

    @Override
    public String deleteAssetunitGroup(AssetunitGroupDeleteDto dto) {
        AssetunitGroup assetunitGroup = new AssetunitGroup();
        BeanUtils.copyProperties(dto,assetunitGroup);
        assetunitGroup.setIsDeleted(IsDeletedEnum.YES.getFlag());

        boolean flag = this.updateById(assetunitGroup);
        if (flag) {
            return "delete success";
        }
        return  "delete failed";
    }

    @Override
    public List<org.orient.otc.api.user.vo.AssetunitGroupVo> getAssetUnitGroupByIds(Set<Integer> ids) {
        LambdaQueryWrapper<AssetunitGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(AssetunitGroup::getId,ids)
                .eq(AssetunitGroup::getIsDeleted,IsDeletedEnum.NO);
        List<AssetunitGroup> list = assetunitGroupMapper.selectList(lambdaQueryWrapper);
        return list.stream().map(item->{
            org.orient.otc.api.user.vo.AssetunitGroupVo vo = new org.orient.otc.api.user.vo.AssetunitGroupVo();
            BeanUtils.copyProperties(item,vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AssetunitVo> getVoByAccounts(Set<String> accounts) {
        List<AssetunitVo> returnList = new ArrayList<>();
        LambdaQueryWrapper<ExchangeAccount> exchangeAccountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        exchangeAccountLambdaQueryWrapper.in(ExchangeAccount::getAccount,accounts)
                .eq(ExchangeAccount::getIsDeleted,IsDeletedEnum.NO);
        List<ExchangeAccount> exchangeAccountList = exchangeAccountMapper.selectList(exchangeAccountLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(exchangeAccountList)) {
            Set<Integer> assetUniteIds = exchangeAccountList.stream().map(item->item.getAssetunitId()).collect(Collectors.toSet());
            LambdaQueryWrapper<Assetunit> assetunitLambdaQueryWrapper = new LambdaQueryWrapper<>();
            assetunitLambdaQueryWrapper.in(Assetunit::getId,assetUniteIds)
                    .eq(Assetunit::getIsDeleted,IsDeletedEnum.NO);
            List<Assetunit> list = assetunitMapper.selectList(assetunitLambdaQueryWrapper);
            returnList = list.stream().map(item->{
                AssetunitVo vo = new AssetunitVo();
                BeanUtils.copyProperties(item,vo);
                return vo;
            }).collect(Collectors.toList());
        }
        return returnList;
    }

    @Override
    public Map<String, AssetunitVo> getMapByAccounts(Set<String> accounts) {
        Map<String, AssetunitVo> returnMap = new HashMap<>();
        LambdaQueryWrapper<ExchangeAccount> exchangeAccountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        exchangeAccountLambdaQueryWrapper.in(ExchangeAccount::getAccount,accounts)
                .eq(ExchangeAccount::getIsDeleted,IsDeletedEnum.NO);
        List<ExchangeAccount> exchangeAccountList = exchangeAccountMapper.selectList(exchangeAccountLambdaQueryWrapper);
        if (null!=exchangeAccountList && exchangeAccountList.size() > 0) {
            Set<Integer> assetUniteIds = exchangeAccountList.stream().map(item->item.getAssetunitId()).collect(Collectors.toSet());
            LambdaQueryWrapper<Assetunit> assetunitLambdaQueryWrapper = new LambdaQueryWrapper<>();
            assetunitLambdaQueryWrapper.in(Assetunit::getId,assetUniteIds)
                    .eq(Assetunit::getIsDeleted,IsDeletedEnum.NO);
            List<Assetunit> list = assetunitMapper.selectList(assetunitLambdaQueryWrapper);
            exchangeAccountList.stream().forEach(exchangeAccount->{
                String account = exchangeAccount.getAccount();
                Integer assetUnitId = exchangeAccount.getAssetunitId();
                if(null != assetUnitId) {
                    Assetunit assetunit = list.stream().filter(item->item.getId().equals(assetUnitId)).findFirst().get();
                    if(null != assetunit) {
                        AssetunitVo vo = new AssetunitVo();
                        BeanUtils.copyProperties(assetunit,vo);
                        returnMap.put(account,vo);
                    }
                }

            });
        }
        return returnMap;
    }
}
