package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.quote.dto.quote.TradeObsDateDto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


/**
 * @author dzrh
 */
@EqualsAndHashCode(callSuper = false)
@ApiModel
@Data
public class TradeMngDTO  implements Serializable {
    private static final long serialVersionUID = 1L;


    private Integer id;
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;

    @ApiModelProperty(value = "组合编号")
    private String combCode;

    /**
     * 关联交易编号
     */
    private String relevanceTradeCode;

    @ApiModelProperty(value = "顺序")
    private Integer sort;

    @ApiModelProperty(value = "交易员id")
    private Integer traderId;

    @ApiModelProperty(value = "簿记ID")
    private Integer assetId;

    @ApiModelProperty(value = "客户ID")
    @NotNull(message = "客户不能为空")
    private Integer clientId;

    @ApiModelProperty(value = "交易日期")
    @NotNull(message = "交易日期不能为空")
    private LocalDate tradeDate;

    /**
     * 产品开始日期
     */
    @ApiModelProperty(value = "产品开始日期")
    private LocalDate productStartDate;

    @ApiModelProperty(value = "到期日")
    @NotNull(message = "到期日不能为空")
    private LocalDate maturityDate;

    @ApiModelProperty(value = "标的合约")
    @NotNull(message = "标的合约不能为空")
    private String underlyingCode;

    @ApiModelProperty(value = "入场价格")
    @NotNull(message = "入场价格不能为空")
    @DecimalMin(value = "0.01",message = "入场价格必须大于0")
    private BigDecimal entryPrice;

    @ApiModelProperty(value = "期权组合类型")
    private OptionCombTypeEnum optionCombType;

    @ApiModelProperty(value = "期权类型")
    private OptionTypeEnum optionType;

    @ApiModelProperty(value = "行权方式")
    private ExerciseTypeEnum exerciseType;

    /**
     * 保底封顶
     */
    @ApiModelProperty(value = "保底封顶",required = true)
    private CeilFloorEnum ceilFloor;

    /**
     * 行权价格
     */
    @ApiModelProperty(value = "行权价格")
    @DecimalMin(value = "0.01",message = "行权价格必须大于0")
    private BigDecimal strike;

    /**
     * 行权价格2
     */
    @ApiModelProperty(value = "行权价格2", required = true)
    @DecimalMin(value = "0.01",message = "行权价格必须大于0")
    private BigDecimal strike2;
    /**
     * 增强价格
     */
    @ApiModelProperty(value = "增强价格")
    @DecimalMin(value = "0.01",message = "增强价格必须大于0")
    private BigDecimal enhancedStrike;

    /**
     * 折扣率
     */
    @ApiModelProperty(value = "折扣率", required = true)
    private BigDecimal discountRate;

    @ApiModelProperty(value = "客户方向")
    private BuyOrSellEnum buyOrSell;

    @ApiModelProperty(value = "看涨看跌")
    private CallOrPutEnum callOrPut;

    /**
     * 期权价格
     */
    @ApiModelProperty(value = "期权价格")
    private BigDecimal optionPremium;

    /**
     * 期权%单价
     */
    @ApiModelProperty(value = "期权%单价")
    private BigDecimal optionPremiumPercent;

    @ApiModelProperty(value = "期权费率是否年化")
    private  Boolean optionPremiumPercentAnnulized;

    /**
     * 保证金￥单价
     */
    @ApiModelProperty(value = "保证金￥单价")
    private BigDecimal margin;

    /**
     * 交易波动率
     */
    @ApiModelProperty(value = "交易波动率")
    private BigDecimal tradeVol;
    /**
     * 计算波动率
     */
    @ApiModelProperty(value = "计算波动率")
    private BigDecimal riskVol;

    /**
     * mid波动率
     */
    @ApiModelProperty(value = "mid波动率")
    private BigDecimal midVol;

    /**
     * mid分红率
     */
    @ApiModelProperty(value = "mid分红率")
    private BigDecimal midDividendYield;

    /**
     * 成交分红率
     */
    @ApiModelProperty(value = "成交分红率")
    private BigDecimal tradeDividendYield;


    /**
     * 无风险利率
     */
    @ApiModelProperty(value = "无风险利率")
    private BigDecimal riskFreeInterestRate;

    /**
     * 成交数量
     */
    @ApiModelProperty(value = "成交数量")
    @Digits(integer = 10,fraction = 4,message = "成交数量输入有误")
    private BigDecimal tradeVolume;

    /**
     * 成交金额
     */
    @ApiModelProperty(value = "成交金额")
    @Digits(integer = 10,fraction = 4,message = "成交金额输入有误")
    private BigDecimal totalAmount;
    /**
     * 名义本金
     */
    @ApiModelProperty(value = "名义本金")
    private BigDecimal notionalPrincipal;
    /**
     * 累计盈亏
     */
    @ApiModelProperty(value = "累计盈亏")
    private BigDecimal totalProfitLoss;

    /**
     * 平仓时间
     */
    @ApiModelProperty(value = "平仓时间")
    private LocalDate closeDate;

    /**
     * Day1 PnL
     */
    @ApiModelProperty(value = "Day1 PnL")
    private BigDecimal day1PnL;

    /**
     * 结算方式
     */
    @ApiModelProperty(value = "结算方式")
    private SettleTypeEnum settleType;

    /**
     * 起始观察日期
     */
    @ApiModelProperty(value = "起始观察日期")
    private LocalDate startObsDate;

    /**
     * 采价次数
     */
    @ApiModelProperty(value = "采价次数")
    private Integer obsNumber;
    /**
     * 每日数量
     */
    @ApiModelProperty(value = "每日数量")
    @DecimalMin(value = "0.01",message = "每日数量必须大于0")
    private BigDecimal basicQuantity;
    /**
     * 杠杆系数
     */
    @ApiModelProperty(value = "杠杆系数")
    @DecimalMin(value = "0",message = "杠杆系数必须大于0")
    private BigDecimal leverage;

    @ApiModelProperty(value = "单位固定赔付")
    private BigDecimal fixedPayment;
    /**
     * 敲出价格
     */
    @ApiModelProperty(value = "敲出价格")
    @DecimalMin(value = "0",message = "敲出价格必须大于或等于0")
    private BigDecimal barrier;

    /**
     * 执行价斜坡
     */
    @ApiModelProperty(value = "执行价斜坡")
    private BigDecimal strikeRamp;

    /**
     * 障碍价斜坡
     */
    @ApiModelProperty(value = "障碍价斜坡")
    private BigDecimal barrierRamp;

    @ApiModelProperty(value = "TTM")
    private BigDecimal ttm;

    @ApiModelProperty(value = "工作日")
    private Integer workday;

    @ApiModelProperty(value = "交易日")
    private Integer tradingDay;

    @ApiModelProperty(value = "公共假日")
    private Integer bankHoliday;

    @ApiModelProperty(value = "交易状态")
    private TradeStateEnum tradeState;

    @ApiModelProperty(value = "pv")
    private BigDecimal pv;

    @ApiModelProperty(value = "delta")
    private BigDecimal delta;

    @ApiModelProperty(value = "gamma")
    private BigDecimal gamma;

    @ApiModelProperty(value = "vega")
    private BigDecimal vega;

    @ApiModelProperty(value = "theta")
    private BigDecimal theta;

    @ApiModelProperty(value = "rho")
    private BigDecimal rho;


    private BigDecimal dividendRho;

    @ApiModelProperty(value = "风险预警信息")
    private String warningMsg;

    @ApiModelProperty(value = "观望日期列表")
    List<TradeObsDateDto> tradeObsDateList;

    @ApiModelProperty(value = "用于前端生成简讯")
    private String tradeKey;


    /**
     * 定价方法：PDE/MC
     */
    @ApiModelProperty(value = "定价方法：PDE/MC")
    private String algorithmName;
    /**
     * 返息率
     */
    @ApiModelProperty(value = "返息率")
    private BigDecimal returnRateStructValue;

    /**
     * 返息率是否年化
     */
    @ApiModelProperty(value = "返息率是否年化")
    private Boolean returnRateAnnulized;

    /**
     * 红利票息
     */
    @ApiModelProperty(value = "红利票息")
    private BigDecimal bonusRateStructValue;

    /**
     * 红利票息是否年化
     */
    @ApiModelProperty(value = "红利票息是否年化")
    private Boolean bonusRateAnnulized;

    /**
     * 敲入障碍
     */
    @ApiModelProperty(value = "敲入障碍")
    private BigDecimal knockinBarrierValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    @ApiModelProperty(value = "敲入障碍是否为相对水平值")
    private Boolean knockinBarrierRelative;
    /**
     * 敲入障碍Shift
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
     * 敲入障碍
     */
    private BigDecimal strike2OnceKnockedinValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    private Boolean strike2OnceKnockedinRelative;
    /**
     * 敲入障碍Shift
     */
    private BigDecimal strike2OnceKnockedinShift;
    /**
     * 保证金占用
     */
    @ApiModelProperty(value = "保证金占用")
    private BigDecimal useMargin;
    /**
     * 障碍是否为相对水平值
     */
    @ApiModelProperty(value = "障碍是否为相对水平值")
    private Boolean barrierRelative;
    /**
     * 障碍Shift
     */
    @ApiModelProperty(value = "障碍Shift")
    private BigDecimal barrierShift;

    /**
     * 敲出票息
     */
    @ApiModelProperty(value = "敲出票息")
    private BigDecimal rebateRate;

    /**
     * 敲出票息是否年化
     */
    @ApiModelProperty(value = "敲出票息是否年化")
    private Boolean rebateRateAnnulized;

    @ApiModelProperty(value = "敲出赔付")
    private BigDecimal knockoutRebate;

    @ApiModelProperty(value = "到期倍数")
    private BigDecimal expireMultiple;

    /**
     * 自定义结构类型
     */
    @ApiModelProperty(value = "自定义结构类型")
    private String structureType;
    /**
     * 自定义期权信息
     */
    @ApiModelProperty(value = "自定义结构信息")
    private String extendInfo;
}
