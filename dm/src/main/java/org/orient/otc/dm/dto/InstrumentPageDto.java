package org.orient.otc.dm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class InstrumentPageDto extends BasePage implements Serializable {
    @ApiModelProperty(value = "合约代码")
    private String instrumentId;
    @ApiModelProperty(value = "交易所代码")
    private String exchangeId;
    @ApiModelProperty(value = "产品代码数组")
    private List<String> productIdList;
    @ApiModelProperty(value = "产品类型")
    private Integer productClass;

    @ApiModelProperty(value = "到期日-开始")
    private String expireDateStart;
    @ApiModelProperty(value = "到期日-结束")
    private String expireDateEnd;

    @ApiModelProperty(value = "合约生命周期状态数组")
    private List<Integer> instLifePhaseList;
    @ApiModelProperty(value = "当前是否交易数组")
    private List<Integer> isTradingList;
    @ApiModelProperty(value = "期权类型数组")
    private List<Integer> optionsTypeList;
    @ApiModelProperty(value = "基础商品代码")
    private String underlyingInstrId;
}
