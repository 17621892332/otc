package org.orient.otc.quote.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 场内持仓校对记录
 */
@Data
public class ExchangePositionCheckVo implements Serializable {

    /**
     * 场内账号
     */
    @ApiModelProperty(value = "场内账号")
    private String investorId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String investorName;

    /**
     * 合约代码
     */
    @ApiModelProperty(value = "合约代码")
    private String instrumentId;

    /**
     * 合约名称
     */
    @ApiModelProperty(value = "合约名称")
    private String instrumentName;

    /**
     * 交易日
     */
    @ApiModelProperty(value = "交易日")
    private LocalDate tradingDay;

    /**
     * 买卖方向
     */
    @ApiModelProperty(value = "买卖方向")
    private String posiDirection;


    /**
     * 校验详情
     */
    @ApiModelProperty(value = "校验详情")
    private String checkMsg;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String status;
}
