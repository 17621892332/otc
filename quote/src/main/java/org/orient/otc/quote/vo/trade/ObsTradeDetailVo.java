package org.orient.otc.quote.vo.trade;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ObsTradeDetailVo extends BaseEntity implements Serializable {
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

    @ApiModelProperty(value = "客户ID")
    private String clientName;

    @ApiModelProperty(value = "期权类型")
    private String optionType;

    @ApiModelProperty(value = "标的合约")
    private String underlyingCode;

    @ApiModelProperty(value = "远期交易编号")
    private String forwardTradeCode;

    @ApiModelProperty(value = "备注")
    private String remarks;

}
