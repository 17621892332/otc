package org.orient.otc.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.AssetunitGroup;
import org.orient.otc.user.vo.AssetunitGroupVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AssetunitGroupService extends IServicePlus<AssetunitGroup> {
    List<AssetunitGroup> getList();

    IPage<AssetunitGroupVo> getListBypage(AssetunitGroupPageListDto dto);

    AssetunitGroup getAssetunitGroupDetail(AssetunitGroupDetailDto dto);

    String addAssetunitGroup(AssetunitGroupAddDto dto);

    String updateAssetunitGroup(AssetunitGroupUpdateDto dto);

    String deleteAssetunitGroup(AssetunitGroupDeleteDto dto);

    List<org.orient.otc.api.user.vo.AssetunitGroupVo> getAssetUnitGroupByIds(Set<Integer> ids);

    // 通过对冲账户的account,查询簿记账户信息
    List<AssetunitVo> getVoByAccounts(Set<String> accounts);


    /**
     * 通过对冲账户的account,查询簿记账户信息 , 返回map
     * @param accounts
     * @return 返回的map中 , key=account , value=簿记账户信息
     */
    Map<String, AssetunitVo> getMapByAccounts(Set<String> accounts);
}
