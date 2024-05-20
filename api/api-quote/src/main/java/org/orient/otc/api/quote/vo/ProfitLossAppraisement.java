package org.orient.otc.api.quote.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.config.BigDecimalFormatter;

import java.math.BigDecimal;


/**
 * 盈亏和估值
 */
@Data
public class ProfitLossAppraisement {

    /**
     * 实现盈亏
     */
    @ApiModelProperty(value = "实现盈亏")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal realizeProfitLoss;

    /**
     * 持仓盈亏
     */
    @ApiModelProperty(value = "持仓盈亏")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal positionProfitLoss;
    /**
     * 持仓市值
     */
    @ApiModelProperty(value = "持仓市值")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal positionValue;
    /**
     * 总资产
     */
    @ApiModelProperty(value = "总资产")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    BigDecimal totalAssets;
}
