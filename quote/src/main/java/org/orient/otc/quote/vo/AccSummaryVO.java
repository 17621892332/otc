package org.orient.otc.quote.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.config.BigDecimalFormatter;

import java.math.BigDecimal;

@Data
public class AccSummaryVO {

    /**
     * 标的代码
     */
    @ApiModelProperty("标的代码")
    private String underlyingCode;

    /**
     * 交易编号
     */
    @ApiModelProperty("交易编号")
    private String tradeCode;
    /**
     * 期权类型
     */
    @ApiModelProperty("期权类型")
    private String optionTypeName;


    /**
     * 行权价格
     */
    @ApiModelProperty("行权价格")
    private BigDecimal strike;

    /**
     * 累计数量
     */
    @ApiModelProperty(value = "累计数量")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING, pattern = "####.##")
    private BigDecimal accumulatedPosition;

    /**
     * 累计固定赔付
     */
    @ApiModelProperty(value = "累计固定赔付")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING, pattern = "####.##")
    private BigDecimal accumulatedPayment;

    /**
     * 累计盈亏
     */
    @ApiModelProperty(value = "累计盈亏")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING, pattern = "####.##")
    private BigDecimal accumulatedPnl;

    /**
     * 当日数量
     */
    @ApiModelProperty(value = "当日数量")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING, pattern = "####.##")
    private BigDecimal todayAccumulatedPosition;

    /**
     * 当日固定赔付
     */
    @ApiModelProperty(value = "当日固定赔付")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING, pattern = "####.##")
    private BigDecimal todayAccumulatedPayment;

    /**
     * 当日盈亏
     */
    @ApiModelProperty(value = "当日盈亏")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING, pattern = "####.##")
    private BigDecimal todayAccumulatedPnl;

    /**
     * 持仓远期数量
     */
    @ApiModelProperty(value = "持仓远期数量")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING, pattern = "####.##")
    private BigDecimal forwardVolume;
    /**
     * 持仓远期盈亏
     */
    @ApiModelProperty(value = "持仓远期盈亏")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING, pattern = "####.##")
    private BigDecimal forwardPnl;
}
