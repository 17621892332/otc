package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.quote.entity.TradeMsg;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("保存简讯信息")
@Data
public class TradeMsgSaveDto {

    @ApiModelProperty(value = "简讯列表",required = true)
    @NotNull(message = "简讯列表不能为空")
    @Valid
    private List<TradeMsg> msgList;
}
