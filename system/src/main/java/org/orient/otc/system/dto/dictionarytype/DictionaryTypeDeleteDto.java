package org.orient.otc.system.dto.dictionarytype;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 删除字典类型dto
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionaryTypeDeleteDto implements Serializable {
    @ApiModelProperty( value = "字典类型ID", required = true)
    @NotEmpty(message = "字典类型ID不能为空")
    private String dicTypeId;

}
