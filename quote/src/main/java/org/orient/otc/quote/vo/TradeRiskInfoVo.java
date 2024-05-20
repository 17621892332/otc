package org.orient.otc.quote.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.common.core.config.BigDecimalFormatter;
import org.orient.otc.common.database.config.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
@ApiModel("风险")
public class TradeRiskInfoVo {

    @ApiModelProperty("id")
    private String id;

    /**
     * 风险日期
     */
    @ApiModelProperty("风险日期")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate riskDate;

    /**
     * 场外or场内
     */
    @ApiModelProperty("场外or场内")
    private TradeRiskCacularResultSourceType tradeRiskCacularResultSourceType;

    private String tradeRiskCacularResultSourceTypeName;
    /**
     * 期权or期货
     */
    @ApiModelProperty("期权or期货")
    private TradeRiskCacularResultType tradeRiskCacularResultType;

    private String tradeRiskCacularResultTypeName;

    /**
     * 组合编号
     */
    @ApiModelProperty("组合编号")
    private String combCode;
    /**
     * 交易编号
     */
    @ApiModelProperty("交易编号")
    private String tradeCode;


    /**
     * 关联交易编号
     */
    private String relevanceTradeCode;
    /**
     * 标的代码
     */
    @ApiModelProperty("标的代码")
    private String underlyingCode;
    /**
     * 标的名称
     */
    @ApiModelProperty("标的名称")
    private String underlyingName;

    /**
     * 场内标的资产码
     */
    @ApiModelProperty("场内标的资产码")
    private String exchangeUnderlyingCode;


    /**
     * 期权代码
     */
    @ApiModelProperty("期权代码")
    private String instrumentId;

    /**
     * 合约实时行情
     */
    @ApiModelProperty("合约实时行情")
    @BigDecimalFormatter
    private BigDecimal lastPrice;

    /**
     * 期权组合类型
     */
    @ApiModelProperty("期权组合类型")
    private OptionCombTypeEnum optionCombType;

    /**
     * 期权组合类型
     */
    @ApiModelProperty("期权组合类型")
    private String optionCombTypeName;
    /**
     * 期权类型
     */
    @ApiModelProperty("期权类型")
    private OptionTypeEnum optionType;


    /**
     * 期权类型
     */
    @ApiModelProperty("期权类型")
    private String optionTypeName;
    /**
     * 看涨看跌
     */
    @ApiModelProperty("看涨看跌")
    private CallOrPutEnum callOrPut;
    /**
     * 看涨看跌
     */
    @ApiModelProperty("看涨看跌")
    private String callOrPutName;

    @ApiModelProperty("入场价格")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal entryPrice;
    /**
     * 行权价格
     */
    @ApiModelProperty("行权价格")
    private BigDecimal strike;

    /**
     * 客户方向
     */
    @ApiModelProperty("客户方向")
    private String clientBuyOrSell;

    /**
     * 东证方向
     */
    @ApiModelProperty("东证方向")
    private BuyOrSellEnum buyOrSell;


    /**
     * 东证方向
     */
    @ApiModelProperty("东证方向")
    private String buyOrSellName;
    /**
     * 到期日
     */
    @ApiModelProperty("到期日")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate maturityDate;

    /**
     * 交易日期
     */
    @ApiModelProperty("交易日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate tradeDate;

    /**
     * 成交数量
     */
    @ApiModelProperty("成交数量")
    private BigDecimal tradeVolume;



    /**
     * 期权￥单价
     */
    @ApiModelProperty("期权￥单价")
    private BigDecimal optionPremium;

    /**
     * 成交金额
     */
    @ApiModelProperty("成交金额")
    private BigDecimal totalAmount;

    /**
     * 名义本金
     */
    @ApiModelProperty(value = "名义本金")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal notionalPrincipal;
    /**
     * 存续单价
     */
    @ApiModelProperty("存续单价")
    private BigDecimal availablePremium;
    /**
     * 存续名义本金
     */
    @ApiModelProperty(value = "存续名义本金")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal availableNotionalPrincipal;

    /**
     * 存续数量
     */
    @ApiModelProperty("存续数量")
    private BigDecimal availableVolume;
    /**
     * 存续总额
     */
    @ApiModelProperty("存续总额")
    private BigDecimal availableAmount;

    /**
     * 今日盈亏
     */
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal todayProfitLoss;
    /**
     * 累计盈亏
     */
    @ApiModelProperty("累计盈亏")
    private BigDecimal totalProfitLoss;


    /**
     * 到期倍数
     */
    @ApiModelProperty(value = "到期倍数")
    private BigDecimal expireMultiple;

    /**
     * 实现盈亏
     */
    @ApiModelProperty(value = "实现盈亏")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private  BigDecimal positionProfitLoss;
    /**
     * 持仓保证金
     */
    @ApiModelProperty(value = "持仓保证金")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal margin;

    /**
     * 开仓波动率
     */
    @ApiModelProperty("开仓波动率")
    private BigDecimal tradeVol;

    /**
     * 当前波动率
     */
    @ApiModelProperty("当前波动率")
    private BigDecimal nowVol;

    /**
     * 波动率覆盖
     */
    @ApiModelProperty(value = "波动率覆盖")
    private BigDecimal riskVol;
    /**
     * 客户ID
     */
    @ApiModelProperty("客户ID")
    private Integer clientId;
    /**
     * 客户信息
     */
    @ApiModelProperty("客户信息")
    private ClientVO client;

    /**
     * 簿记id
     */
    @ApiModelProperty("簿记账户ID")
    private Integer assetId;

    /**
     * 簿记账户信息
     */
    @ApiModelProperty("簿记账户信息")
    private AssetunitVo assetunit;

    /**
     * 累计头寸
     */
    @ApiModelProperty(value = "累计头寸")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "###0.00")
    private BigDecimal accumulatedPosition;

    /**
     * 累计赔付
     */
    @ApiModelProperty(value = "累计赔付")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "###0.00")
    private BigDecimal accumulatedPayment;

    /**
     * 累计敲出盈亏
     */
    @ApiModelProperty(value = "累计敲出盈亏")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "###0.00")
    private BigDecimal accumulatedPnl;

    /**
     * 今日头寸
     */
    @ApiModelProperty(value = "今日头寸")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "###0.00")
    private BigDecimal todayAccumulatedPosition;

    /**
     * 今日赔付
     */
    @ApiModelProperty(value = "今日赔付")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "###00.00")
    private BigDecimal todayAccumulatedPayment;

    /**
     * 今日敲出盈亏
     */
    @ApiModelProperty(value = "今日敲出盈亏")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "###0.00")
    private BigDecimal todayAccumulatedPnl;

    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal deltaLots;

    private BigDecimal delta;

    private BigDecimal deltaCash;

    private BigDecimal gammaLots;

    private BigDecimal gamma;

    private BigDecimal gammaCash;

    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal theta;

    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal vega;


    /**
     * 无风险利率变化对期权价值的影响
     */
    private BigDecimal rho;

    /**
     * 股息率变化对期权价值的影响
     */
    private BigDecimal dividendRho;

    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal day1PnL;
    /**
     * 观察日列表
     */
    private LocalDate startObsDate;
    /**
     * 观察日列表
     */
    private List<TradeObsDateVO> obsDateList;
    /**
     *
     * 结算方式
     */
    @ApiModelProperty(value = "结算方式")
    private SettleTypeEnum settleType;

    /**
     * 结算方式
     */
    @ApiModelProperty(value = "结算方式")
    private String settleTypeName;

    /**
     * 采价次数
     */
    @ApiModelProperty(value = "采价次数")
    private Integer obsNumber;

    /**
     * 敲出价格
     */
    @ApiModelProperty(value = "敲出价格")
    private BigDecimal barrier;
    /**
     * 单日数量
     */
    @ApiModelProperty(value = "单日数量")
    private BigDecimal basicQuantity;
    /**
     * 杠杆系数
     */
    @ApiModelProperty(value = "杠杆系数")
    private BigDecimal leverage;

    /**
     * 固定赔付
     */
    private BigDecimal fixedPayment;

    /**
     * 敲出赔付
     */
    @ApiModelProperty(value = "敲出赔付")
    private BigDecimal knockoutRebate;


    @ApiModelProperty(value = "融行账号")
    private String account;
    /**
     * 最后一次计算的状态
     */
    @ApiModelProperty("状态")
    private SuccessStatusEnum status;



    /**
     * 是否敲入
     */
    private Boolean alreadyKnockedIn;
    /**
     * 是否敲入
     */
    private String alreadyKnockedInFormat;
    /**
     * 红利票息
     */
    private BigDecimal bonusRateStructValue;

    /**
     * 红利票息是否年化
     */
    private Boolean bonusRateAnnulized;

    private BigDecimal bonusRateStructValueFormat;

    @ApiModelProperty(value = "敲入价格")
    private BigDecimal knockinBarrierValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    private Boolean knockinBarrierRelative;

    @ApiModelProperty(value = "敲入价格")
    private String knockinBarrierValueFormat;

    /**
     * 敲入行权价格是否为相对水平值
     */
    private Boolean strikeOnceKnockedinRelative;

    @ApiModelProperty(value = "敲入行权价格1")
    private BigDecimal strikeOnceKnockedinValue;

    @ApiModelProperty(value = "敲入行权价格1")
    private String strikeOnceKnockedinValueFormat;

    /**
     * 敲入行权价格2是否为相对水平值
     */
    private Boolean strike2OnceKnockedinRelative;

    @ApiModelProperty(value = "敲入行权价格2")
    private BigDecimal strike2OnceKnockedinValue;

    @ApiModelProperty(value = "敲入行权价格2")
    private String strike2OnceKnockedinValueFormat;

    /**
     * 敲出价格格式化
     */
    @ApiModelProperty(value = "敲出价格")
    private String barrierValueFormat;

    /**
     * 敲出观察日
     */
    @ApiModelProperty(value = "敲出观察日")
    private String barrierObsDateFormat;

    /**
     * 敲出票息
     */
    @ApiModelProperty(value = "敲出票息")
    private String rebateRateFormat;


}
