package org.orient.otc.dm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class Instrument extends BaseEntity  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="instrumentId",type= IdType.NONE)
    @ApiModelProperty(value = "合约代码")
    private String instrumentId;
    @ApiModelProperty(value = "合约名称")
    private String instrumentName;
    @ApiModelProperty(value = "交易所代码")
    private String exchangeId;
    @ApiModelProperty(value = "产品代码")
    private String productId;
    /**
     * 1 期货 2期权
     */
    @ApiModelProperty(value = "产品类型")
    private Integer productClass;
    @ApiModelProperty(value = "合约数量乘数")
    private Integer volumeMultiple;
    @ApiModelProperty(value = "最小变动价位")
    private BigDecimal priceTick;
    @ApiModelProperty(value = "到期日")
    private String expireDate;
    @ApiModelProperty(value = "合约生命周期状态")
    private Integer instLifePhase;
    @ApiModelProperty(value = "当前是否交易")
    private Integer isTrading;
    @ApiModelProperty(value = "执行价")
    private BigDecimal strikePrice;
    @ApiModelProperty(value = "期权类型")
    private Integer optionsType;
    @ApiModelProperty(value = "基础商品代码")
    private String underlyingInstrId;
}
