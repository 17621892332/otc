package org.orient.otc.api.quote.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.common.core.config.BigDecimalFormatter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class TradeMngVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */

    private Integer id;
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;

    /**
     * 关联交易编号
     */
    private String relevanceTradeCode;

    @ApiModelProperty(value = "组合编号")
    private String combCode;

    @ApiModelProperty(value = "顺序")
    private Integer sort;

    @ApiModelProperty(value = "交易员id")
    private Integer traderId;
    @ApiModelProperty(value = "交易员名称")
    private String traderName;
    @ApiModelProperty(value = "簿记ID")
    private Integer assetId;

    @ApiModelProperty(value = "簿记名称")
    private String assetName;

    @ApiModelProperty(value = "客户ID")
    private Integer clientId;
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    @ApiModelProperty(value = "交易日期")

    private LocalDate tradeDate;

    /**
     * 产品开始日期
     */
    @ApiModelProperty(value = "产品开始日期")
    private LocalDate productStartDate;

    @ApiModelProperty(value = "到期日")
    private LocalDate maturityDate;

    @ApiModelProperty(value = "标的合约")
    private String underlyingCode;

    @ApiModelProperty(value = "入场价格")
    private BigDecimal entryPrice;

    @ApiModelProperty(value = "期权组合类型code")
    private OptionCombTypeEnum optionCombType;

    @ApiModelProperty(value = "期权组合类型")
    private String optionCombTypeName;

    /**
     * 名义本金
     */
    @ApiModelProperty(value = "名义本金")
    private BigDecimal notionalPrincipal;

    /**
     * 存续名义本金
     */
    @ApiModelProperty(value = "存续名义本金")
    private BigDecimal availableNotionalPrincipal;

    @ApiModelProperty(value = "存续数量")
    private BigDecimal availableVolume;

    @ApiModelProperty(value = "累计盈亏")
    private BigDecimal totalProfitLoss;

    @ApiModelProperty(value = "平仓时间")
    private LocalDate closeDate;
    @ApiModelProperty(value = "期权类型")
    private OptionTypeEnum optionType;

    @ApiModelProperty(value = "期权类型")
    private String optionTypeName;

    @ApiModelProperty(value = "行权方式")
    private ExerciseTypeEnum exerciseType;

    @ApiModelProperty(value = "行权方式")
    private String exerciseTypeName;
    /**
     * 保底封顶
     */
    @ApiModelProperty(value = "保底封顶",required = true)
    private CeilFloorEnum ceilFloor;
    /**
     * 保底封顶
     */
    @ApiModelProperty(value = "保底封顶",required = true)
    private String ceilFloorName;


    /**
     * 行权价格
     */
    @ApiModelProperty(value = "行权价格")
    private BigDecimal strike;

    /**
     * 行权价格2
     */
    @ApiModelProperty(value = "行权价格2")
    private BigDecimal strike2;
    /**
     * 增强价格
     */
    @ApiModelProperty(value = "增强价格")
    private BigDecimal enhancedStrike;


    /**
     * 折扣率
     */
    @ApiModelProperty(value = "折扣率")
    private BigDecimal discountRate;

    @ApiModelProperty(value = "客户方向")
    private BuyOrSellEnum buyOrSell;

    @ApiModelProperty(value = "客户方向")
    private String buyOrSellName;

    @ApiModelProperty(value = "看涨看跌")
    private CallOrPutEnum callOrPut;

    @ApiModelProperty(value = "看涨看跌")
    private String callOrPutName;

    @ApiModelProperty(value = "期权￥单价")
    private BigDecimal optionPremium;

    @ApiModelProperty(value = "期权%单价")
    private BigDecimal optionPremiumPercent;

    /**
     * 期权费率是否年化
     */
    @ApiModelProperty(value = "期权费率是否年化")
    private  Boolean optionPremiumPercentAnnulized;

    @ApiModelProperty(value = "保证金￥单价")
    private BigDecimal margin;

    @ApiModelProperty(value = "交易波动率")
    private BigDecimal tradeVol;

    @ApiModelProperty(value = "mid波动率")
    private BigDecimal midVol;

    @ApiModelProperty(value = "计算波动率")
    private BigDecimal riskVol;

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

    @ApiModelProperty(value = "成交数量")
    @Digits(integer = 10,fraction = 4,message = "成交数量输入有误")
    private BigDecimal tradeVolume;

    @ApiModelProperty(value = "成交金额")
    @Digits(integer = 10,fraction = 4,message = "成交金额输入有误")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "Day1 PnL")
    private BigDecimal day1PnL;
    @ApiModelProperty(value = "结算方式")
    private SettleTypeEnum settleType;

    @ApiModelProperty(value = "结算方式")
    private String settleTypeName;
    @ApiModelProperty(value = "起始观察日期")
    private LocalDate startObsDate;
    @ApiModelProperty(value = "采价次数")
    private Integer obsNumber;
    @ApiModelProperty(value = "每日数量")
    private BigDecimal basicQuantity;
    @ApiModelProperty(value = "杠杆系数")
    private BigDecimal leverage;
    @ApiModelProperty(value = "单位固定赔付")
    private BigDecimal fixedPayment;

    @ApiModelProperty(value = "敲出价格")
    @BigDecimalFormatter
    private BigDecimal barrier;

    @ApiModelProperty(value = "执行价斜坡")
    private BigDecimal strikeRamp;

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

    @ApiModelProperty(value = "交易状态")
    private String tradeStateName;

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

    /**
     * 用于前端生成简讯
     */
    @ApiModelProperty(value = "用于前端生成简讯")
    private String tradeKey;

    /**
     * 是否同步: 0未同步 1已同步 2同步失败
     */
    @ApiModelProperty(value = "是否同步: 0未同步 1已同步 2同步失败")
    private Integer isSync;

    /**
     * 同步信息
     */
    @ApiModelProperty(value = "同步信息")
    private String syncMsg;
    /**
     * 观望日期列表
     */
    @ApiModelProperty(value = "观望日期列表")
    List<TradeObsDateVO> tradeObsDateList;

    @ApiModelProperty(value = "风险预警信息")
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
    @ApiModelProperty(value = "保证金占用")
    private BigDecimal useMargin;

    /**
     * 敲出赔付
     */
    @ApiModelProperty(value = "敲出赔付")
    private BigDecimal knockoutRebate;

    /**
     * 到期倍数
     */
    @ApiModelProperty(value = "到期倍数")
    private BigDecimal expireMultiple;

    @ApiModelProperty("品种名称")
    private String varietyName;
    @ApiModelProperty(value = "操作人")
    private Integer updatorId;
    @ApiModelProperty(value = "操作人名称")
    private String updatorName;
    @ApiModelProperty(value = "操作时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "平仓数量")
    private BigDecimal closeVolume;
    @ApiModelProperty(value = "平仓标的价格")
    private BigDecimal closeEntryPrice;
    @ApiModelProperty(value = "平仓价格")
    private BigDecimal closePrice;
    @ApiModelProperty(value = "平仓金额")
    private BigDecimal closeTotalAmount;
    @ApiModelProperty(value = "平仓记录")
    List<TradeCloseMngVO> tradeCloseMngVOList;


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
    /**
     * 交易单位
     */
    @ApiModelProperty(value = "交易单位")
    private String quoteUnit;
}
