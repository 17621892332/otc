package org.orient.otc.system.dto.dictionary;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 获取字典
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionaryGetByIdDto implements Serializable {

    /**
     * 字典ID
     */
    @NotEmpty(message = "dicId不能为空")
    private String dicId;
}
