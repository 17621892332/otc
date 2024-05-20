package org.orient.otc.system.service;

import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.system.dto.structure.StructureAddDTO;
import org.orient.otc.system.dto.structure.StructureEditDTO;
import org.orient.otc.system.entity.StructureInfo;
import org.orient.otc.api.system.vo.StructureInfoVO;

import java.util.List;

/**
 * 自定义结构服务
 */
public interface StructureService  extends IServicePlus<StructureInfo> {

    /**
     * 新增自定义结构
     * @param addDTO 自定义结构内容
     * @return 非0成功
     */
    Integer addStructure(StructureAddDTO addDTO);
    /**
     * 编辑自定义结构
     * @param editDTO 自定义结构内容
     * @return 非0成功
     */
    Integer editStructure(StructureEditDTO editDTO);

    /**
     * 删除自定义结构
     * @param id 结构ID
     * @return 非0成功
     */
    Integer delStructure(Integer id);

    /**
     * 获取自定义结构列表
     * @return 自定义结构
     */
    List<StructureInfoVO> getStructureInfoList();
}
