package org.orient.otc.system.dto.dictionary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 新增字典内容
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionaryPageDto extends BasePage implements Serializable {

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
    private String dicValue;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    private String dicName;

}
