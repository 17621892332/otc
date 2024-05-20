package org.orient.otc.api.system.dto.tradedatachangerecord;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.List;

@Data
public class TradeDataChangeRecordPageDto extends BasePage {
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
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private List<Integer> clientIdList;
    /**
     * 簿记账户ID
     */
    @ApiModelProperty(value = "簿记账户ID")
    private List<Integer> assetunitIdList;
    /**
     * 变更类型
     */
    @ApiModelProperty(value = "变更类型")
    private List<DataChangeTypeEnum> typeList;

    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态")
    private List<TradeStateEnum> tradeStateList;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private List<Integer> userIdList;
}
