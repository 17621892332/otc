package org.orient.otc.api.quote.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
@ApiModel
public class TradeObsDateVO {

    @ApiModelProperty(value = "交易ID")
    private Integer tradeId;

    private String underlyingCode;
    /**
     * 观察日期
     */
    @ApiModelProperty(value = "观察日期")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate obsDate;

    /**
     * 结算日期
     */
    @ApiModelProperty(value = "结算日期")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate settlementDate;

    /**
     * 结算天数
     */
    private Integer dayCount;

    /**
     * 观察价格
     */
    @ApiModelProperty(value = "观察价格")
    private BigDecimal price;

    /**
     * 障碍价格
     */
    @ApiModelProperty(value = "障碍价格")
    private BigDecimal barrier;

    /**
     * 敲出价格字符串展示
     */
    @ApiModelProperty(value = "敲出价格字符串展示")
    private String barrierString;

    /**
     * 百分比
     */
    @ApiModelProperty(value = "百分比")
    private String barrierRate;

    /**
     * 障碍是否为相对水平值
     */
    @ApiModelProperty(value = "障碍是否为相对水平值")
    private Boolean barrierRelative;
    /**
     * 障碍Shift
     */
    @ApiModelProperty(value = "障碍Shift")
    private BigDecimal barrierShift;

    /**
     * 敲出票息
     */
    @ApiModelProperty(value = "敲出票息")
    private BigDecimal rebateRate;

    /**
     * 敲出票息String
     */
    @ApiModelProperty(value = "敲出票息String")
    private String rebateRateString;

    /**
     * 敲出票息是否年化
     */
    @ApiModelProperty(value = "敲出票息是否年化")
    private Boolean rebateRateAnnulized;

}
