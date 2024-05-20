package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.quote.enums.TradeTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dzrh
 */
@ApiModel("录入交易")
@Data
public class TradeInsertDTO {
    @ApiModelProperty(value = "交易类型",required = true)
    @NotNull(message = "交易类型不能为空")
    private TradeTypeEnum tradeType;

    /**
     * 交易方向: 0 客户 1东证
     */
    @ApiModelProperty(value = "交易方向: 0 客户 1东证")
    private Integer tradeDirection;
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;

    @ApiModelProperty(value = "交易员id")
    @NotNull(message = "交易员不能为空")
    private Integer traderId;

    @ApiModelProperty(value = "簿记ID")
    @NotNull(message = "簿记不能为空")
    private Integer assetId;

    @ApiModelProperty(value = "交易列表",required = true)
    @NotNull(message = "交易列表不能为空")
    @Valid
    private List<TradeMngDTO> tradeList;
}
