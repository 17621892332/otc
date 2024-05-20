package org.orient.otc.system.dto.dictionary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 修改字典内容
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionaryUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 字典ID
     */
    @NotEmpty(message = "dicId不能为空")
    private String dicId;
    /**
     * 字典类型code
     */
    @ApiModelProperty(value = "字典类型code", required = true)
    @NotEmpty(message = "字典类型code不能为空")
    private String dicTypeCode;

    /**
     * 字典值
     */
    @ApiModelProperty(value = "字典值", required = true)
    @NotEmpty(message = "字典值不能为空")
    private String dicValue;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    @NotEmpty(message = "名称不能为空")
    private String dicName;
    /**
     * 字典排序
     */
    @ApiModelProperty(value = "字典排序", required = true)
    private Integer dicSort;
}
