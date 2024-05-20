package org.orient.otc.quote.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 填充word模板VO
 * @author dzrh
 */
@Data
public class WordTemplateVO {
    /**
     * 基本信息map , 包含第一层表格中的待填充数据
     */
    Map<String,String> baseInfoDataMap;
    /**
     * 第一个第二层表格待填充数据
     */
    List<Map<String,String>> table1DataList;
    /**
     * 第二个第二层表格待填充数据
     */
    List<Map<String,String>> table2DataList;
}
