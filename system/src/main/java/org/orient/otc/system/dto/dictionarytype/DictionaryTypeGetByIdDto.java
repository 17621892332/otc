package org.orient.otc.system.dto.dictionarytype;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 获取字典类型
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionaryTypeGetByIdDto implements Serializable {

    @NotEmpty(message = "dicTypeId不能为空")
    private String dicTypeId;
}
