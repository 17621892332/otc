package org.orient.otc.api.system.dto.tradedatachangerecord;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TradeDataChangeRecordDetailDto{
    /**
     * ID
     */
    @ApiModelProperty(value = "变更ID")
    @NotNull(message = "变更ID不能为空")
    private Integer id;

}
