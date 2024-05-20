package org.orient.otc.api.system.dto.tradedatachangerecord;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 交易记录变更记录
 */
@Data
public class TradeDataChangeRecordDTO {
    /**
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号")
    @NotEmpty(message = "交易编号不能为空")
    private String tradeCode ;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    @NotNull(message = "客户不能为空")
    private Integer clientId ;
    /**
     * 簿记账户ID
     */
    @ApiModelProperty(value = "簿记账户ID")
    @NotNull(message = "簿记账户不能为空")
    private Integer assetunitId ;
    /**
     * 变更类型
     */
    @ApiModelProperty(value = "变更类型")
    @NotNull(message = "变更类型不能为空")
    private DataChangeTypeEnum changeType ;
    /**
     * 变更字段
     */
    @ApiModelProperty(value = "变更字段")
    @NotEmpty(message = "变更字段不能为空")
    private String changeFields ;
    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态")
    @NotNull(message = "交易状态不能为空")
    private TradeStateEnum tradeState;
}
