package org.orient.otc.dm.service;

import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.dm.dto.variety.VarietyTypeAddDto;
import org.orient.otc.dm.dto.variety.VarietyTypeDeleteDto;
import org.orient.otc.dm.dto.variety.VarietyTypeEditDto;
import org.orient.otc.dm.entity.VarietyType;

/**
 * 品种信息(VarietyType)表服务接口
 *
 * @author makejava
 * @since 2023-07-14 11:18:49
 */
public interface VarietyTypeService extends IServicePlus<VarietyType> {
   String addVarietyType(VarietyTypeAddDto addDto);

    String editVarietyType(VarietyTypeEditDto editDto);

   String deleteVarietyType(VarietyTypeDeleteDto deleteDto);
}

