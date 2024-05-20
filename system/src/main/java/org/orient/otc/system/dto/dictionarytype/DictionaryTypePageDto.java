package org.orient.otc.system.dto.dictionarytype;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 字典类型分页dto
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionaryTypePageDto extends BasePage implements Serializable {

    /**
     * 字典类型code
     */
    @ApiModelProperty( value = "字典类型code", required = true)
    private String dicTypeCode;

    /**
     * 名称
     */
    @ApiModelProperty( value = "名称", required = true)
    private String dicTypeName;
    /**
     * 描述
     */
    @ApiModelProperty( value = "描述", required = true)
    private String deScript;
}
