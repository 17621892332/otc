package org.orient.otc.api.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 字典内容
 */
@Data
public class DictionaryVo{
    /**
     * 字典类型code
     */
    @ApiModelProperty(value = "字典类型code", required = true)
    private String dicTypeCode;
    /**
     * 字典值
     */
    @ApiModelProperty(value = "字典值", required = true)
    private String dicValue;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    private String dicName;
    /**
     * 字典排序
     */
    @ApiModelProperty(value = "字典排序", required = true)
    private Integer dicSort;
}
