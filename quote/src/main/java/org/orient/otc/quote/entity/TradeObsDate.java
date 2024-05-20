package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class TradeObsDate extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    private Integer tradeId;

    private String underlyingCode;


    /**
     * 结算日期
     */
    private LocalDate settlementDate;

    /**
     * 结算天数
     */
    private Integer dayCount;

    private LocalDate obsDate;

    private BigDecimal price;
    /**
     * 障碍价格
     */
    @ApiModelProperty(value = "障碍价格")
    private BigDecimal barrier;

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
     * 敲出票息是否年化
     */
    @ApiModelProperty(value = "敲出票息是否年化")
    private Boolean rebateRateAnnulized;

}
