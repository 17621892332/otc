package org.orient.otc.user.feign;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.user.feign.AssetUnitClient;
import org.orient.otc.api.user.vo.AssetunitGroupVo;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.user.entity.Assetunit;
import org.orient.otc.user.service.AssetunitGroupService;
import org.orient.otc.user.service.AssetunitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/assetUnit")
@Slf4j
public class AssetUnitFeignController implements AssetUnitClient {
    @Autowired
    AssetunitService assetunitService;

    @Autowired
    AssetunitGroupService assetunitGroupService;


    /**
     * 获取簿记账户列表
     * @param idSet 簿记账户ID
     * @return 簿记账户
     */
    @Override
    public List<AssetunitVo> getAssetUnitList(Set<Integer> idSet) {
        LambdaQueryWrapper<Assetunit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(idSet!=null && !idSet.isEmpty(),Assetunit::getId, idSet);
        queryWrapper.eq(Assetunit::getIsDeleted,IsDeletedEnum.NO);
        return assetunitService.listVo(queryWrapper, AssetunitVo.class);
    }

    /**
     * @param idSet
     * @return
     */
    @Override
    public Map<Integer, String> getAssetUnitMapByIds(Set<Integer> idSet) {
        List<Assetunit> list = assetunitService.queryByIds(idSet);
        return list.stream().collect(Collectors.toMap(Assetunit::getId, Assetunit::getName));
    }

    /**
     * @param id
     * @return
     */
    @Override
    public AssetunitVo getAssetunitById(Integer id) {
        return assetunitService.getVoById(id, AssetunitVo.class);
    }

    @Override
    public List<AssetunitVo> getAssetunitByGroupIds(Set<Integer> ids) {
        LambdaQueryWrapper<Assetunit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Assetunit::getGroupId,ids);
        queryWrapper.eq(Assetunit::getIsDeleted, IsDeletedEnum.NO);
        return assetunitService.listVo(queryWrapper, AssetunitVo.class);
    }

    @Override
    public AssetunitGroupVo getAssetUnitGroupById(Integer id) {
        AssetunitGroupVo vo  = assetunitGroupService.getVoById(id,AssetunitGroupVo.class);
        return vo;
    }

    @Override
    public List<AssetunitGroupVo> getAssetUnitGroupByIds(Set<Integer> ids) {
        return assetunitGroupService.getAssetUnitGroupByIds(ids);
    }

    @Override
    public List<AssetunitVo> getVoByAccounts(Set<String> accounts) {
        return assetunitGroupService.getVoByAccounts(accounts);
    }

    @Override
    public Map<String, AssetunitVo> getMapByAccounts(Set<String> accounts) {
        return assetunitGroupService.getMapByAccounts(accounts);
    }
}
