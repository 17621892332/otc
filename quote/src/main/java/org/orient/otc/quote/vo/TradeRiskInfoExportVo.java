package org.orient.otc.quote.vo;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.SuccessStatusEnum;
import org.orient.otc.common.core.config.BigDecimalFormatter;
import org.orient.otc.common.database.config.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
@ApiModel("风险导出")
public class TradeRiskInfoExportVo {
    @Alias("结算日期     ")
    private LocalDate riskDate;
    @Alias("交易编号        ")
    private String tradeCode;
    @Alias("标的合约")
    private String underlyingCode;
    @Alias("标的价格")
    private BigDecimal lastPrice;
    @Alias("期权类型")
    private String optionType;
    @Alias("期权代码")
    private String instrumentId;
    @Alias("看涨看跌")
    private String callOrPut;
    @Alias("行权价格")
    private BigDecimal strike;
    @Alias("入场价格")
    private BigDecimal entryPrice;
    @Alias("东证方向")
    private String buyOrSell;
    @Alias("交易日期     ")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tradeDate;
    @Alias("到期日      ")
    private LocalDate maturityDate;
    @Alias("存续数量")
    private BigDecimal availableVolume;
    @Alias(value = "名义本金")
    private String notionalPrincipal;
    @Alias(value = "存续名义本金")
    private String availableNotionalPrincipal;
    @Alias("成交单价")
    private BigDecimal optionPremium;
    @Alias("成交总额")
    private String totalAmount;
    @Alias("存续单价")
    private BigDecimal availablePremium;
    @Alias("存续总额")
    private String availableAmount;
    @Alias("持仓保证金")
    private String margin;
    @Alias("客户名称")
    private String clientName;
    @Alias("开仓波动率")
    private BigDecimal tradeVol;
    @Alias("当前波动率")
    private BigDecimal nowVol;
    @Alias(value = "波动率覆盖")
    private BigDecimal riskVol;
    @Alias(value = "TodayPnl  ")
    private BigDecimal todayPnl;
    @Alias(value = "day1PnL  ")
    private BigDecimal day1PnL;
    @Alias(value = "delta(Lots)   ")
    private String deltaLots;
    @Alias(value = "delta(Quant)   ")
    private BigDecimal delta;
    @Alias(value = "delta(Cash) ")
    private String deltaCash;
    @Alias(value = "Gamma(Lots)      ")
    private String gammaLots;
    @Alias(value = "Gamma(Quant)        ")
    private String gamma;
    @Alias(value = "Gamma(1%cash)   ")
    private String gammaCash;
    @Alias(value = "theta    ")
    private String theta;
    @ApiModelProperty("vega    ")
    private String vega;
    @Alias(value = "rho    ")
    private String rho;
    @Alias(value = "dividendRho     ")
    private String dividendRho;
    @Alias("簿记名称")
    private String assetunitName;
    @Alias(value = "融航账号")
    private String account;
    @Alias(value = "结算方式")
    private String settleType;
    @Alias(value = "单日数量")
    private BigDecimal basicQuantity;
    @Alias(value = "杠杆系数")
    private BigDecimal leverage;
    @Alias(value = "敲出价格")
    private BigDecimal barrier;
    @Alias(value = "敲出赔付")
    private BigDecimal knockoutRebate;
    @Alias(value = "敲入价格")
    private BigDecimal knockinBarrierValue;
    @Alias(value = "敲入行权价格1")
    private BigDecimal strikeOnceKnockedinValue;
    @Alias(value = "敲入行权价格2")
    private BigDecimal strike2OnceKnockedinValue;
    @Alias("组合类型")
    private String optionCombType;
    @Alias("组合编号          ")
    private String combCode;

    @Alias(value = "累计数量")
    private String accumulatedPosition;
    @Alias(value = "累计固定赔付")
    private String accumulatedPayment;
    @Alias(value = "累计盈亏")
    private String accumulatedPnl;
    @Alias(value = "当日数量")
    private String todayAccumulatedPosition;
    @Alias(value = "当日固定赔付")
    private String todayAccumulatedPayment;
    @Alias(value = "当日盈亏")
    private String todayAccumulatedPnl;

}
