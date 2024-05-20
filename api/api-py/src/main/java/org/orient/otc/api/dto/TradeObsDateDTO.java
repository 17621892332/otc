package org.orient.otc.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 观察日数据
 */
@Data
public class TradeObsDateDTO implements Serializable {

    /**
     * 观察日期
     */
    @ApiModelProperty(value = "观察日期")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime obsDate;

    /**
     * 结算日期
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime settlementDate;

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
    private BigDecimal callBarrier;

    /**
     * 障碍Shift
     */
    @ApiModelProperty(value = "障碍Shift")
    private BigDecimal callBarriershift;

    /**
     * 敲出票息
     */
    @ApiModelProperty(value = "敲出票息")
    private BigDecimal coupon;

    /**
     * 敲出票息是否年化
     */
    @ApiModelProperty(value = "敲出票息是否年化")
    private Boolean isCouponannualized;
}
