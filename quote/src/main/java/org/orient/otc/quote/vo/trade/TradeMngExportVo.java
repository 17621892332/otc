package org.orient.otc.quote.vo.trade;

import cn.hutool.core.annotation.Alias;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class TradeMngExportVo implements Serializable {
    @Alias(value = "交易状态")
    private String tradeState;
    @Alias(value = "交易编号")
    private String tradeCode;
    @Alias(value = "组合编号")
    private String combCode;
    @Alias(value = "簿记账户")
    private String assetName;
    @Alias(value = "客户名称")
    private String clientName;
    @Alias(value = "标的合约")
    private String underlyingCode;
    @Alias("品种类型")
    private String varietyName;
    @Alias(value = "组合类型")
    private String optionCombTypeName;
    @Alias(value = "期权类型")
    private String optionTypeName;
    @Alias(value = "看涨看跌")
    private String callOrPutName;
    @Alias(value = "行权价格")
    private BigDecimal strike;
    @Alias(value = "东证方向")
    private String buyOrSellName;
    @Alias(value = "交易日期")
    private LocalDate tradeDate;
    @Alias(value = "到期日期")
    private LocalDate maturityDate;
    @Alias(value = "入场价格")
    private BigDecimal entryPrice;
    @Alias(value = "期权价格")
    private BigDecimal optionPremium;
    @Alias(value = "成交数量")
    private BigDecimal tradeVolume;
    @Alias(value = "存续数量")
    private BigDecimal availableVolume;
    @Alias(value = "成交金额")
    private BigDecimal totalAmount;
    @Alias(value = "交易员")
    private String traderName;
    @Alias(value = "备注")
    private String syncMsg;
    @Alias(value = "风险预警信息")
    private String warningMsg;
}
