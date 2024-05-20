package org.orient.otc.quote.dto.trade;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TradeSettlementConfirmBookQueryDTO extends BasePage {
    /**
     * 交易编号
     */
    @ApiModelProperty("交易编号")
    private String  tradeCode;

    /**
     * 簿记ID
     */
    @ApiModelProperty(value = "簿记ID",required = true)
    private List<Integer>  assetIdList;

    /**
     * 簿记组ID
     */
    @ApiModelProperty(value = "簿记组ID",required = true)
    private List<Integer>  assetGroupList;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID",required = true)
    private  List<Integer> clientIdList;

    /**
     * 交易员ID
     */
    @ApiModelProperty(value = "交易员ID",required = true)
    private  List<Integer> traderIdList;

    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型",required = true)
    private List<OptionTypeEnum> optionTypeList;

    /**
     * 标的合约
     */
    @ApiModelProperty(value = "标的合约",required = true)
    private List<String> underlyingCodeList;

    /**
     * 客户方向
     */
    @ApiModelProperty(value = "客户方向",required = true)
    @EnumValue
    private BuyOrSellEnum buyOrSell;

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

}
