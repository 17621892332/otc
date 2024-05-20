package org.orient.otc.quote.dto.collateral;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.CollateralEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("抵押品-审核dto")
public class CollateralCheckDto {

    @ApiModelProperty(value = "id")
    @NotEmpty(message = "ID不能为空")
    private List<Integer> idList;

    @ApiModelProperty(value = "执行状态")
    @NotNull(message = "执行状态不能为空")
    private CollateralEnum.ExecuteStatusEnum executeStatus;


}
