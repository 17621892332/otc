package org.orient.otc.system.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.system.dto.structure.StructureAddDTO;
import org.orient.otc.system.dto.structure.StructureEditDTO;
import org.orient.otc.system.entity.StructureInfo;
import org.orient.otc.system.mapper.StructureInfoMapper;
import org.orient.otc.system.service.StructureService;
import org.orient.otc.api.system.vo.StructureInfoVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现
 */
@Service
public class StructureServiceImpl extends ServiceImpl<StructureInfoMapper, StructureInfo> implements StructureService {
    @Override
    public Integer addStructure(StructureAddDTO addDTO) {
        StructureInfo structureInfo = CglibUtil.copy(addDTO,StructureInfo.class);
        return this.getBaseMapper().insert(structureInfo);
    }

    @Override
    public Integer editStructure(StructureEditDTO editDTO) {
        StructureInfo structureInfo = CglibUtil.copy(editDTO,StructureInfo.class);
        return this.getBaseMapper().updateById(structureInfo);
    }

    @Override
    public Integer delStructure(Integer id) {
        LambdaQueryWrapper<StructureInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StructureInfo::getId,id);
        queryWrapper.eq(StructureInfo::getIsDeleted, IsDeletedEnum.NO);
        StructureInfo structureInfo = new StructureInfo();
        structureInfo.setIsDeleted(IsDeletedEnum.YES.getFlag());
        return  this.getBaseMapper().update(structureInfo,queryWrapper);
    }

    @Override
    public List<StructureInfoVO> getStructureInfoList() {
        return this.listVo(new LambdaQueryWrapper<StructureInfo>().eq(StructureInfo::getIsDeleted,IsDeletedEnum.NO)
                        .orderByAsc(StructureInfo::getSortIndex)
                , StructureInfoVO.class);
    }
}
