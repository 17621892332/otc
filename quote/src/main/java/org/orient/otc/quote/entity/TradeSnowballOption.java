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
public class TradeSnowballOption extends BaseEntity implements Serializable {

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 交易ID
     */
    private Integer tradeId;

    /**
     * 定价方法 PDE MC
     */
    private String algorithmName;

    /**
     * 返息率
     */
    private BigDecimal returnRateStructValue;

    /**
     * 返息率是否年化 0 否 1是
     */
    private Boolean returnRateAnnulized;

    /**
     * 红利票息
     */
    private BigDecimal bonusRateStructValue;

    /**
     * 红利票息是否年化
     */
    private Boolean bonusRateAnnulized;

    /**
     * 敲入价格
     */
    private BigDecimal knockinBarrierValue;

    /**
     * 敲入价格是否为相对水平值
     */
    private Boolean knockinBarrierRelative;
    /**
     * 敲入价格Shift
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
     * 敲入障碍2
     */
    private BigDecimal strike2OnceKnockedinValue;

    /**
     * 敲入障碍2是否为相对水平值
     */
    private Boolean strike2OnceKnockedinRelative;
    /**
     * 敲入障碍2Shift
     */
    private BigDecimal strike2OnceKnockedinShift;

    /**
     * 保证金占用
     */
    @ApiModelProperty(value = "保证金占用")
    private BigDecimal useMargin;

}
