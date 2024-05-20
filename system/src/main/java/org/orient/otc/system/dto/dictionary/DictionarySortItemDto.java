package org.orient.otc.system.dto.dictionary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class DictionarySortItemDto implements Serializable {
    /**
     * 字典ID
     */
    @NotEmpty(message = "dicId不能为空")
    @ApiModelProperty( value = "字典dicId", required = true)
    private String dicId;

    @NotEmpty(message = "排序不能为空")
    @ApiModelProperty( value = "序号", required = true)
    private Integer dicSort;

}
