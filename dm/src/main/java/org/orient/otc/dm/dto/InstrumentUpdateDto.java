package org.orient.otc.dm.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class InstrumentUpdateDto implements Serializable {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "合约代码")
    @NotBlank(message = "合约代码不能为空")
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
