package org.orient.otc.system.dto.clientdatachangerecord;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.List;

@Data
public class ClientDataChangeRecordPageDto extends BasePage {
    /**
     * 操作的开始日期
     */
    @ApiModelProperty(value = "操作的开始日期")
    private LocalDate startDate;
    /**
     * 操作的结束日期
     */
    @ApiModelProperty(value = "操作的结束日期")
    private LocalDate endDate;

    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    private String clientCode;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private List<Integer> clientIdList;
    /**
     * 变更类型
     */
    @ApiModelProperty(value = "变更类型")
    private List<DataChangeTypeEnum> typeList;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private List<Integer> userIdList;
}
