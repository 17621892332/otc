package org.orient.otc.dm.dto.underlying;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.dm.enums.UnderlyingState;

@Data
@ApiModel
public class UnderlyingManagerQueryDto {
    @ApiModelProperty(value = "标的品种id")
    private Integer varietyId;
    @ApiModelProperty(value = "标的状态")
    private UnderlyingState underlyingState;
}
