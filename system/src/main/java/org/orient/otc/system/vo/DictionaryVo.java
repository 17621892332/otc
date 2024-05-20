package org.orient.otc.system.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 字典内容
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
public class DictionaryVo implements Serializable {

    private static final long serialVersionUID = 1L;
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
