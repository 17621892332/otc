package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.CallOrPutEnum;
import org.orient.otc.api.quote.enums.ExerciseTypeEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TradeQueryDTO extends BasePage {


    @ApiModelProperty(value = "簿记ID",required = true)
    private List<Integer>  assetIdList;

    @ApiModelProperty(value = "客户ID",required = true)
    private  List<Integer> clientIdList;

    @ApiModelProperty(value = "交易开始日期",required = true)
    private LocalDate startTradeDate;
    @ApiModelProperty(value = "交易结束日期",required = true)
    private LocalDate endTradeDate;

    @ApiModelProperty(value = "到期开始日期",required = true)
    private LocalDate startMaturityDate;
    @ApiModelProperty(value = "到期结束日期",required = true)
    private LocalDate endMaturityDate;

    @ApiModelProperty(value = "平仓开始日期",required = true)
    private LocalDate startCloseDate;
    @ApiModelProperty(value = "平仓结束日期",required = true)
    private LocalDate endCloseDate;

    @ApiModelProperty(value = "敲出开始日期",required = true)
    private LocalDate startKnockOutDate;
    @ApiModelProperty(value = "敲出结束日期",required = true)
    private LocalDate endKnockOutDate;
    @ApiModelProperty(value = "组合代码",required = true)
    private String combCode;

    @ApiModelProperty("交易编号")
    private String  tradeCode;

    @ApiModelProperty(value = "标的合约",required = true)
    private List<String> underlyingCodeList;

    @ApiModelProperty(value = "品种ID",required = true)
    private  List<Integer>  varietyIdList;

    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态",required = true)
    private List<TradeStateEnum> tradeStateList;

    @ApiModelProperty(value = "期权类型",required = true)
    private List<OptionTypeEnum> optionTypeList;

    @ApiModelProperty(value = "风险预警信息")
    private String warningMsg;

    @ApiModelProperty(value = "簿记账户组ID",required = true)
    private List<Integer>  assetGroupIdList;
    @ApiModelProperty("合约编号")
    private String  instrumentId;
    /**
     * 交易方向:  买入|卖出
     */
    @ApiModelProperty(value = "交易方向")
    private  String buySell;

    /**
     * 交易员
     */
    @ApiModelProperty("交易员")
    private List<Integer>  traderIdList;

    /**
     * 行权方式
     */
    @ApiModelProperty(value = "行权方式")
    ExerciseTypeEnum exerciseType;

    /**
     * 看涨看跌
     */
    @ApiModelProperty(value = "看涨看跌")
    private CallOrPutEnum callOrPut;
}
