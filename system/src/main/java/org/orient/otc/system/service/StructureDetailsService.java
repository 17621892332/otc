package org.orient.otc.system.service;

import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.system.dto.structure.StructureDetailsAddDTO;
import org.orient.otc.system.dto.structure.StructureDetailsEditDTO;
import org.orient.otc.system.entity.StructureDetails;
import org.orient.otc.system.vo.structure.StructureDetailsVO;

import java.util.List;

/**
 * 自定义结构详情服务
 */
public interface StructureDetailsService extends IServicePlus<StructureDetails> {

    /**
     * 新增结构详情
     * @param addDTO 自定义结构详情
     * @return 非0成功
     */
    Integer addStructureDetails(StructureDetailsAddDTO addDTO);
    /**
     * 编辑自定义结构详情
     * @param editDTO 自定义结构详情
     * @return 非0成功
     */
    Integer editStructureDetails(StructureDetailsEditDTO editDTO);

    /**
     * 删除自定义结构详情
     * @param id 结构详情ID
     * @return 非0成功
     */
    Integer delStructureDetails(Integer id);

    /**
     * 获取结构详情列表
     * @param structureId 结构ID
     * @return 结构详情
     */
    List<StructureDetailsVO> getStructureDetailsList(Integer structureId);
}
