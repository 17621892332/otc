package org.orient.otc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.system.dto.dictionarytype.*;
import org.orient.otc.system.entity.DictionaryType;

import java.util.List;

/**
 * 字典类型服务
 */
public interface DictionaryTypeService extends IServicePlus<DictionaryType> {
    /**
     * 字典类型服务列表
     * @return 字典类型
     */
    List<DictionaryType> getDictionaryTypeList();

    String add(DictionaryTypeAddDto dto);

    String update(DictionaryTypeUpdateDto dto);

    String delete(DictionaryTypeDeleteDto dto);

    String updateSort(DictionaryTypeSortDto dto);

    IPage<DictionaryType> selectByPage(DictionaryTypePageDto dto);
}
