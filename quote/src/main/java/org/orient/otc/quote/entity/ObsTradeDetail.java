package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class ObsTradeDetail extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "观察日期")
    private LocalDate obsDate;

    @ApiModelProperty(value = "交易编号")
    private String tradeCode;

    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @ApiModelProperty(value = "期权类型")
    private OptionTypeEnum optionType;

    @ApiModelProperty(value = "标的合约")
    private String underlyingCode;

    @ApiModelProperty(value = "远期交易编号")
    private String forwardTradeCode;

    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 是否平仓
     */
    @TableField(exist = false)
    private Boolean isClose;

}
