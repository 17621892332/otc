package org.orient.otc.system.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.system.dto.structure.StructureDetailsAddDTO;
import org.orient.otc.system.dto.structure.StructureDetailsEditDTO;
import org.orient.otc.system.entity.StructureDetails;
import org.orient.otc.system.mapper.StructureDetailsMapper;
import org.orient.otc.system.service.StructureDetailsService;
import org.orient.otc.system.vo.structure.StructureDetailsVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现
 */
@Service
public class StructureDetailsServiceImpl extends ServiceImpl<StructureDetailsMapper, StructureDetails> implements StructureDetailsService {

    @Override
    public Integer addStructureDetails(StructureDetailsAddDTO addDTO) {
        StructureDetails structureDetails = CglibUtil.copy(addDTO,StructureDetails.class);
        return this.getBaseMapper().insert(structureDetails);
    }

    @Override
    public Integer editStructureDetails(StructureDetailsEditDTO editDTO) {
        StructureDetails structureDetails = CglibUtil.copy(editDTO,StructureDetails.class);
        return this.getBaseMapper().updateById(structureDetails);
    }

    @Override
    public Integer delStructureDetails(Integer id) {
        LambdaQueryWrapper<StructureDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StructureDetails::getId,id);
        queryWrapper.eq(StructureDetails::getIsDeleted, IsDeletedEnum.NO);
        StructureDetails structureDetails = new StructureDetails();
        structureDetails.setIsDeleted(IsDeletedEnum.YES.getFlag());
        return  this.getBaseMapper().update(structureDetails,queryWrapper);
    }



    @Override
    public List<StructureDetailsVO> getStructureDetailsList(Integer structureId) {
        return this.listVo(new LambdaQueryWrapper<StructureDetails>()
                .eq(StructureDetails::getIsDeleted,IsDeletedEnum.NO)
                .eq(StructureDetails::getStructureId,structureId), StructureDetailsVO.class);
    }
}
