package org.orient.otc.system.dto.dictionary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 删除字典内容
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionaryDeleteDto implements Serializable {

    /**
     * 字典ID
     */
    @NotEmpty(message = "dicId不能为空")
    private String dicId;
}
