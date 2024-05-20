package org.orient.otc.system.dto.capitaldatachangerecord;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.common.core.dto.BasePage;

@Data
public class CapitalDataChangeRecordPageDto extends BasePage {
    /**
     * 资金记录ID
     */
    @ApiModelProperty(value = "资金记录ID")
    private Integer capitalId ;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;
    /**
     * 交易编号
     */
    private String tradeCode;
    /**
     * 标的代码
     */
    private String underlyingCode;
    /**
     * 资金方向
     */
    private CapitalDirectionEnum direction;

}
