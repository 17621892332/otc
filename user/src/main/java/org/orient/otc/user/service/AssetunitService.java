package org.orient.otc.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.Assetunit;
import org.orient.otc.user.vo.AssetunitVo;
import org.orient.otc.user.vo.TraderVo;

import java.util.List;
import java.util.Set;

public interface AssetunitService extends IServicePlus<Assetunit> {
    List<Assetunit> getList();
    List<TraderVo> getTraderList();
    /**
     * 获取对冲账户对应的簿记账户
     * @param ids 簿记账户ID
     * @return key账号ID value 簿记信息
     */
    List<Assetunit> queryByIds(Set<Integer> ids);

    IPage<AssetunitVo> getListByPage(AssetunitPageListDto dto);

    String addAssetunit(AssetunitAddDto dto);

    String updateAssetunit(AssetunitUpdateDto dto);

    String deleteAssetunit(AssetunitDeleteDto dto);

    AssetunitVo getAssetunitDetail(AssetunitDetailDto dto);
}
