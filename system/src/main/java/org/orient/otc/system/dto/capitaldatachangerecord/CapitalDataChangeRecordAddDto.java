package org.orient.otc.system.dto.capitaldatachangerecord;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;

/**
 * 资金历史记录新增DTO
 */
@Data
public class CapitalDataChangeRecordAddDto {
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId ;
    /**
     * 资金记录ID
     */
    @ApiModelProperty(value = "资金记录ID")
    private Integer capitalId ;
    /**
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号")
    private String tradeCode ;
    /**
     * 标的代码
     */
    @ApiModelProperty(value = "标的代码")
    private String underlyingCode;

    /**
     * 资金方向
     */
    @ApiModelProperty(value = "资金方向")
    private CapitalDirectionEnum direction;
    /**
     * 变更类型
     */
    @ApiModelProperty(value = "变更类型")
    private DataChangeTypeEnum changeType ;
    /**
     * 变更字段json字符串(应该是一个json数组)
     */
    @ApiModelProperty(value = "变更字段")
    private String changeFields ;
}
