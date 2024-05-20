package org.orient.otc.quote.vo.settlementreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 账户总览返回值VO
 */
@Data
public class AccountOverviewVO {

    /**
     * 收支与结存
     */
    @ApiModelProperty(value = "收支与结存")
    private InOutBalance inOutBalance;

    /**
     * 占用与可取
     */
    @ApiModelProperty(value = "占用与可取")
    private OccupyDesirable occupyDesirable;

    /**
     * 盈亏和估值
     */
    @ApiModelProperty(value = "盈亏和估值")
    private ProfitLossAppraisement profitLossAppraisement;

}
