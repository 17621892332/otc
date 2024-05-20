package org.orient.otc.quote.dto.trade;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
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
public class TradeConfirmBookQueryDTO extends BasePage {
    /**
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号",required = true)
    private String  tradeCode;

    /**
     * 合约编号
     */
    @ApiModelProperty(value = "合约编号",required = true)
    private String contractCode;
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

    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态",required = true)
    private List<TradeStateEnum> tradeStateList;
}
