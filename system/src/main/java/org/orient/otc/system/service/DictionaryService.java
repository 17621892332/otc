package org.orient.otc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.system.dto.dictionary.*;
import org.orient.otc.system.entity.Dictionary;
import org.orient.otc.system.vo.DictionaryVo;

import java.util.List;
import java.util.Map;

public interface DictionaryService extends IServicePlus<Dictionary> {
    /**
     * 获取字典列表
     * @param dictTypeCode 字典类型
     * @return 字典列表
     */
    List<Dictionary> getList(String dictTypeCode);

    String add(DictionaryAddDto dto);

    String updateDictionary(DictionaryUpdateDto dto);

    String deleteDictionary(DictionaryDeleteDto dto);

    String updateSort(DictionarySortDto dto);

    IPage<Dictionary> selectByPage(DictionaryPageDto dto);

    Map<String, List<DictionaryVo>> getListAll();

    String getDictionaryValue(String type, String name);

    String getDictionaryName(String type, String code);

    List<DictionaryVo> getListByDictTypeCode(String dictTypeCode);

    Map<String,String> getDictionaryMap();
}
