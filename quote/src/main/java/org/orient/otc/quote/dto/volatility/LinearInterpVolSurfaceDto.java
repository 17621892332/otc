package org.orient.otc.quote.dto.volatility;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 波动率插值参数
 */
@Data
@ApiModel
public class LinearInterpVolSurfaceDto {

    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型")
    @NotNull(message = "期权类型不能为空")
    private OptionTypeEnum optionType;
    /**
     * 标的合约code
     */
    @ApiModelProperty("标的合约code")
    @NotNull(message = "标的合约不能为空")
    private String underlyingCode;

    /**
     * 客户买卖方向
     */
    @ApiModelProperty("客户买卖方向")
    @NotNull(message = "客户买卖方向不能为空")
    private BuyOrSellEnum buyOrSell;

    /**
     * 入场价格
     */
    @ApiModelProperty(value = "入场价格",required = true)
    @NotNull(message = "入场价格不能为空")
    @DecimalMin(value = "0.01",message = "入场价格必须大于0")
    private BigDecimal entryPrice;

    /**
     * 行权价格
     */
    @ApiModelProperty(value = "行权价格",required = true)
    @NotNull(message = "行权价格不能为空")
    @DecimalMin(value = "0.01",message = "行权价格必须大于0")
    private BigDecimal strike;

    /**
     * 交易日期
     */
    @ApiModelProperty(value = "交易日期")
    @NotNull(message = "交易日期不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate tradeDate;

    /**
     * 到期日
     */
    @ApiModelProperty(value = "到期日")
    @NotNull(message = "到期日不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate maturityDate;

    /**
     * 批量的时候使用 , 与每一个入参匹配
     */
    @ApiModelProperty(value = "序号")
    private String no;
}
