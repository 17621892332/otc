package org.orient.otc.system.feign;

import org.orient.otc.api.system.feign.DictionaryClient;
import org.orient.otc.system.entity.Dictionary;
import org.orient.otc.system.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dzrh
 */
@RestController
@RequestMapping("/dictionary")
public class DictionaryFeignController implements DictionaryClient {
    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 获取字典内容列表
     * @param dictTypeCode 字典值
     * @return key 字典值 value 描述
     */
    @Override
    public Map<String, String> getDictionaryMapByIds(String dictTypeCode) {
        List<Dictionary> list = dictionaryService.getList(dictTypeCode);
        return  list.stream().collect(Collectors.toMap(Dictionary::getDicValue,Dictionary::getDicName));
    }

    @Override
    public Map<String, String>getDictionaryMap() {
        return  dictionaryService.getDictionaryMap();
    }
}
