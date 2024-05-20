package org.orient.otc.dm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.api.dm.enums.AssetTypeEnum;
import org.orient.otc.api.dm.vo.VarietyVo;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.dm.dto.variety.VarietyAddDto;
import org.orient.otc.dm.dto.variety.VarietyEditDto;
import org.orient.otc.dm.dto.variety.VarietyIdDto;
import org.orient.otc.dm.dto.variety.VarietyQueryDto;
import org.orient.otc.dm.entity.Variety;
import org.orient.otc.dm.vo.VarietyByTraderIdVO;
import org.orient.otc.dm.vo.VarietyByVarietyTypeVO;

import java.util.List;
import java.util.Set;

public interface VarietyService extends IServicePlus<Variety> {
    List<Variety> getList();

    Page<VarietyVo> queryVarietyList(VarietyQueryDto varietyQueryDto);

    String addVariety(VarietyAddDto addDto);

    String editVariety(VarietyEditDto editDto);

    String deleteVariety(VarietyIdDto varietyIdDto);

    /**
     * 获取品种列表
     * @param idSet 品种ID
     * @return 品种列表
     */
    List<VarietyVo> queryVarietyListById(Set<Integer> idSet);

    VarietyVo getVarietyById(Integer varietyId);

    /**
     * 通过资产类型获取品种列表
     * @param assetType 资产类型
     * @return 品种列表
     */
    List<Variety> getVarietListByAssetType(AssetTypeEnum assetType);

    /**
     * 获取风险品种筛选条件
     * @return 风险筛选条件
     */
    List<VarietyByVarietyTypeVO> getVarietyByVarietyTypeList();


    /**
     * 获取风险品种筛选条件
     * @return 风险筛选条件
     */
    List<VarietyByTraderIdVO> getVarietyByTraderIdList();
}
