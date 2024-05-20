package org.orient.otc.dm.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.dm.dto.variety.VarietyTypeAddDto;
import org.orient.otc.dm.dto.variety.VarietyTypeDeleteDto;
import org.orient.otc.dm.dto.variety.VarietyTypeEditDto;
import org.orient.otc.dm.entity.VarietyType;
import org.orient.otc.dm.mapper.VarietyTypeMapper;
import org.orient.otc.dm.service.VarietyTypeService;
import org.springframework.stereotype.Service;

/**
 * 品种信息(VarietyType)表服务实现类
 *
 * @author makejava
 * @since 2023-07-14 11:18:49
 */
@Service("varietyTypeService")
public class VarietyTypeServiceImpl extends ServiceImpl<VarietyTypeMapper, VarietyType> implements VarietyTypeService {

    @Override
    public String addVarietyType(VarietyTypeAddDto addDto) {
        VarietyType varietyType = CglibUtil.copy(addDto,VarietyType.class);
       int c= this.getBaseMapper().insert(varietyType);
       if (c>0){
           return "新增成功";
       }else {
           return "新增失败";
       }
    }

    @Override
    public String editVarietyType(VarietyTypeEditDto editDto) {
        VarietyType varietyType = CglibUtil.copy(editDto,VarietyType.class);
        int c= this.getBaseMapper().updateById(varietyType);
        if (c>0){
            return "保存成功";
        }else {
            return "保存失败";
        }
    }

    @Override
    public String deleteVarietyType(VarietyTypeDeleteDto deleteDto) {
        VarietyType varietyType = new VarietyType();
        varietyType.setIsDeleted(IsDeletedEnum.YES.getFlag());
        varietyType.setId(deleteDto.getId());
        int c= this.getBaseMapper().updateById(varietyType);
        if (c>0){
            return "删除成功";
        }else {
            return "删除失败";
        }
    }
}

