package org.orient.otc.api.quote.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class BuildConfirmBookInfoVO implements Serializable {
    @ApiModelProperty(value = "期权类型对应的模板文件路径")
    private String path;
    /**
     * 确认书名称 : 交易确认书-客户名称-交易日期-期权类型
     */
    @ApiModelProperty(value = "确认书名称")
    private String name;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "交易日期")
    private String tradeDate;

    @ApiModelProperty(value = "期权类型")
    private OptionTypeEnum optionType;

    @ApiModelProperty(value = "模板zip文件名称")
    private String zipName;

}
