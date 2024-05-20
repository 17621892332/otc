package org.orient.otc.api.system.dto.clientdatachangerecord;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author chengqiang
 */

@Data
public class ClientDataChangeRecordDetailDto{
    /**
     * ID
     */
    @ApiModelProperty(value = "变更ID")
    @NotNull(message = "变更ID不能为空")
    private Integer id;

}
