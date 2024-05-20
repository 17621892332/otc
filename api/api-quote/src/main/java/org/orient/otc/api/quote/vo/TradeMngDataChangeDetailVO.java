package org.orient.otc.api.quote.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.common.core.config.BigDecimalFormatter;
import org.orient.otc.common.core.util.FieldAlias;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class TradeMngDataChangeDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */

    private Integer id;
    @FieldAlias(value = "交易编号")
    private String tradeCode;

    @FieldAlias(value = "组合编号")
    private String combCode;

    @FieldAlias(value = "顺序")
    private Integer sort;

    @FieldAlias(value = "交易员id")
    private Integer traderId;
    @FieldAlias(value = "交易员名称")
    private String traderName;
    @FieldAlias(value = "簿记ID")
    private Integer assetId;

    @FieldAlias(value = "簿记名称")
    private String assetName;

    @FieldAlias(value = "客户ID")
    private Integer clientId;
    @FieldAlias(value = "客户名称")
    private String clientName;
    @FieldAlias(value = "交易日期")

    private LocalDate tradeDate;

    /**
     * 产品开始日期
     */
    @FieldAlias(value = "产品开始日期")
    private LocalDate productStartDate;

    @FieldAlias(value = "到期日")
    private LocalDate maturityDate;

    @FieldAlias(value = "标的合约")
    private String underlyingCode;

    @FieldAlias(value = "入场价格")
    private BigDecimal entryPrice;

    @FieldAlias(value = "期权组合类型code")
    private OptionCombTypeEnum optionCombType;

    @FieldAlias(value = "期权组合类型")
    private String optionCombTypeName;

    /**
     * 名义本金
     */
    @FieldAlias(value = "名义本金")
    private BigDecimal notionalPrincipal;

    /**
     * 存续名义本金
     */
    @FieldAlias(value = "存续名义本金")
    private BigDecimal availableNotionalPrincipal;

    @FieldAlias(value = "存续数量")
    private BigDecimal availableVolume;

    @FieldAlias(value = "累计盈亏")
    private BigDecimal totalProfitLoss;

    @FieldAlias(value = "成本")
    private BigDecimal cost;

    @FieldAlias(value = "平仓时间")
    private LocalDate closeDate;
    @FieldAlias(value = "期权类型")
    private OptionTypeEnum optionType;

    @FieldAlias(value = "期权类型")
    private String optionTypeName;

    @FieldAlias(value = "行权方式")
    private ExerciseTypeEnum exerciseType;

    @FieldAlias(value = "行权方式")
    private String exerciseTypeName;

    @FieldAlias(value = "行权价格")
    private BigDecimal strike;

    @FieldAlias(value = "客户方向")
    private BuyOrSellEnum buyOrSell;

    @FieldAlias(value = "客户方向")
    private String buyOrSellName;

    @FieldAlias(value = "看涨看跌")
    private CallOrPutEnum callOrPut;

    @FieldAlias(value = "看涨看跌")
    private String callOrPutName;

    @FieldAlias(value = "期权￥单价")
    private BigDecimal optionPremium;

    @FieldAlias(value = "期权%单价")
    private BigDecimal optionPremiumPercent;

    /**
     * 期权费率是否年化
     */
    @FieldAlias(value = "期权费率是否年化")
    private  Boolean optionPremiumPercentAnnulized;

    @FieldAlias(value = "保证金￥单价")
    private BigDecimal margin;

    @FieldAlias(value = "交易波动率")
    private BigDecimal tradeVol;

    @FieldAlias(value = "mid波动率")
    private BigDecimal midVol;

    @FieldAlias(value = "计算波动率")
    private BigDecimal riskVol;

    /**
     * mid分红率
     */
    @FieldAlias(value = "mid分红率")
    private BigDecimal midDividendYield;
    /**
     * 成交分红率
     */
    @FieldAlias(value = "成交分红率")
    private BigDecimal tradeDividendYield;

    /**
     * 无风险利率
     */
    @FieldAlias(value = "无风险利率")
    private BigDecimal riskFreeInterestRate;

    @FieldAlias(value = "成交数量")
    @Digits(integer = 10,fraction = 4,message = "成交数量输入有误")
    private BigDecimal tradeVolume;

    @FieldAlias(value = "成交金额")
    @Digits(integer = 10,fraction = 4,message = "成交金额输入有误")
    private BigDecimal totalAmount;

    @FieldAlias(value = "Day1 PnL")
    private BigDecimal day1PnL;
    @FieldAlias(value = "结算方式")
    private SettleTypeEnum settleType;

    @FieldAlias(value = "结算方式")
    private String settleTypeName;
    @FieldAlias(value = "起始观察日期")
    private LocalDate startObsDate;
    @FieldAlias(value = "采价次数")
    private Integer obsNumber;
    @FieldAlias(value = "每日数量")
    private BigDecimal basicQuantity;
    @FieldAlias(value = "杠杆系数")
    private BigDecimal leverage;
    @FieldAlias(value = "单位固定赔付")
    private BigDecimal fixedPayment;

    @FieldAlias(value = "敲出价格")
    @BigDecimalFormatter
    private BigDecimal barrier;

    @FieldAlias(value = "执行价斜坡")
    private BigDecimal strikeRamp;

    @FieldAlias(value = "障碍价斜坡")
    private BigDecimal barrierRamp;

    @FieldAlias(value = "TTM")
    private BigDecimal ttm;

    @FieldAlias(value = "工作日")
    private Integer workday;

    @FieldAlias(value = "交易日")
    private Integer tradingDay;

    @FieldAlias(value = "公共假日")
    private Integer bankHoliday;

    @FieldAlias(value = "交易状态")
    private TradeStateEnum tradeState;

    @FieldAlias(value = "交易状态")
    private String tradeStateName;

    @FieldAlias(value = "pv")
    private BigDecimal pv;

    @FieldAlias(value = "delta")
    private BigDecimal delta;

    @FieldAlias(value = "gamma")
    private BigDecimal gamma;

    @FieldAlias(value = "vega")
    private BigDecimal vega;

    @FieldAlias(value = "theta")
    private BigDecimal theta;

    @FieldAlias(value = "rho")
    private BigDecimal rho;

    private BigDecimal dividendRho;

    /**
     * 用于前端生成简讯
     */
    @FieldAlias(value = "用于前端生成简讯")
    private String tradeKey;

    /**
     * 是否同步: 0未同步 1已同步 2同步失败
     */
    @FieldAlias(value = "是否同步: 0未同步 1已同步 2同步失败")
    private Integer isSync;

    /**
     * 同步信息
     */
    @FieldAlias(value = "同步信息")
    private String syncMsg;
    /**
     * 观望日期列表
     */
    @FieldAlias(value = "观望日期列表")
    List<TradeObsDateVO> tradeObsDateList;

    @FieldAlias(value = "风险预警信息")
    private String warningMsg;

    /**
     * 定价方法：PDE/MC
     */
    private String algorithmName;

    /**
     * 返息率
     */
    private BigDecimal returnRateStructValue;

    /**
     * 返息率是否年化 0 否 1是
     */
    private Boolean returnRateAnnulized;

    /**
     * 红利票息
     */
    private BigDecimal bonusRateStructValue;

    /**
     * 红利票息是否年化
     */
    private Boolean bonusRateAnnulized;

    /**
     * 敲入价格
     */
    private BigDecimal knockinBarrierValue;

    /**
     * 敲入价格是否为相对水平值
     */
    private Boolean knockinBarrierRelative;
    /**
     * 敲入价格Shift
     */
    private BigDecimal knockinBarrierShift;

    /**
     * 是否敲入
     */
    private Boolean alreadyKnockedIn;

    /**
     * 敲入障碍
     */
    private BigDecimal strikeOnceKnockedinValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    private Boolean strikeOnceKnockedinRelative;
    /**
     * 敲入障碍Shift
     */
    private BigDecimal strikeOnceKnockedinShift;

    /**
     * 敲入障碍2
     */
    private BigDecimal strike2OnceKnockedinValue;

    /**
     * 敲入障碍2是否为相对水平值
     */
    private Boolean strike2OnceKnockedinRelative;
    /**
     * 敲入障碍2Shift
     */
    private BigDecimal strike2OnceKnockedinShift;

    /**
     * 保证金占用
     */
    @FieldAlias(value = "保证金占用")
    private BigDecimal useMargin;

    /**
     * 敲出赔付
     */
    @FieldAlias(value = "敲出赔付")
    private BigDecimal knockoutRebate;

    /**
     * 到期倍数
     */
    @FieldAlias(value = "到期倍数")
    private BigDecimal expireMultiple;

    @FieldAlias("品种名称")
    private String varietyName;
    @FieldAlias(value = "操作人")
    private Integer updatorId;
    @FieldAlias(value = "操作人名称")
    private String updatorName;

    @FieldAlias(value = "平仓数量")
    private BigDecimal closeVolume;
    @FieldAlias(value = "平仓标的价格")
    private BigDecimal closeEntryPrice;
    @FieldAlias(value = "平仓价格")
    private BigDecimal closePrice;
    @FieldAlias(value = "平仓金额")
    private BigDecimal closeTotalAmount;
    @FieldAlias(value = "平仓记录")
    List<TradeCloseMngVO> tradeCloseMngVOList;
}
