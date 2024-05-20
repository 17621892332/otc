package org.orient.otc.quote.vo.volatility;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class LinearInterpVolSurfaceVo {
    @ApiModelProperty(value = "交易波动率",required = true)
    private BigDecimal tradeVol;

    @ApiModelProperty(value = "mid波动率",required = true)
    private BigDecimal midVol;

    /**
     * 批量调用的时候使用 , 与每一个入参匹配
     */
    @ApiModelProperty(value = "序号")
    private String no;
}
