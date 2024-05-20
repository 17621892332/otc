package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dzrh
 */
@Data
public class SnowballOptionDTO {

    /**
     * 定价方法：PDE/MC
     */
    @ApiModelProperty(value = "定价方法：PDE/MC")
    private String algorithmName;
    /**
     * 返息率
     */
    @ApiModelProperty(value = "返息率")
    private BigDecimal returnRateStructValue;

    /**
     * 返息率是否年化
     */
    @ApiModelProperty(value = "返息率是否年化")
    private Boolean returnRateAnnulized;

    /**
     * 红利票息
     */
    @ApiModelProperty(value = "红利票息")
    private BigDecimal bonusRateStructValue;

    /**
     * 红利票息是否年化
     */
    @ApiModelProperty(value = "红利票息是否年化")
    private Boolean bonusRateAnnulized;

    /**
     * 敲入障碍
     */
    private BigDecimal knockinBarrierValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    private Boolean knockinBarrierRelative;
    /**
     * 敲入障碍Shift
     */
    private BigDecimal knockinBarrierShift;

    /**
     * 是否敲入
     */
    private Boolean alreadyKnockedIn;


    /**
     * 敲入障碍
     */
    private BigDecimal strikeOnceKnockedinValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    private Boolean strikeOnceKnockedinRelative;
    /**
     * 敲入障碍Shift
     */
    private BigDecimal strikeOnceKnockedinShift;


    /**
     * 敲入障碍
     */
    private BigDecimal strike2OnceKnockedinValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    private Boolean strike2OnceKnockedinRelative;
    /**
     * 敲入障碍Shift
     */
    private BigDecimal strike2OnceKnockedinShift;

}
