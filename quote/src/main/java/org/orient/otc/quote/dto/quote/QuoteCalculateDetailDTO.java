package org.orient.otc.quote.dto.quote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 定价计算明细
 */
@Data
@ApiModel(value = "定价计算明细")
public class QuoteCalculateDetailDTO {

    /**
     * 顺序
     */
    @ApiModelProperty(value = "顺序")
    private Integer sort;

    /**
     * 标的合约
     */
    @ApiModelProperty(value = "标的合约")
    @NotBlank(message = "标的合约不能为空")
    private String underlyingCode;

    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型", required = true)
    @NotNull(message = "期权不能为空")
    private OptionTypeEnum optionType;

    /**
     * 看涨看跌
     */
    @ApiModelProperty(value = "看涨看跌", required = true)
    private CallOrPutEnum callOrPut;

    /**
     * 保底封顶
     */
    @ApiModelProperty(value = "保底封顶",required = true)
    private CeilFloorEnum ceilFloor;

    /**
     * 入场价格
     */
    @ApiModelProperty(value = "入场价格", required = true)
    @NotNull(message = "入场价格不能为空")
    @DecimalMin(value = "0.01",message = "入场价格必须大于0")
    private BigDecimal entryPrice;

    /**
     * 行权价格
     */
    @ApiModelProperty(value = "行权价格", required = true)
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
    @ApiModelProperty(value = "增强价格", required = true)
    @DecimalMin(value = "0.01",message = "增强价格必须大于0")
    private BigDecimal enhancedStrike;

    /**
     * 折扣率
     */
    @ApiModelProperty(value = "折扣率", required = true)
    private BigDecimal discountRate;

    /**
     * 波动率
     */
    @ApiModelProperty(value = "波动率", required = true)
    private BigDecimal tradeVol;

    /**
     * 成交分红率
     */
    @ApiModelProperty(value = "成交分红率")
    private BigDecimal tradeDividendYield;

    /**
     * mid波动率
     */
    @ApiModelProperty(value = "mid波动率")
    private BigDecimal midVol;

    @ApiModelProperty(value = "ttm", required = true)
    private BigDecimal ttm;

    @ApiModelProperty(value = "定价日期")
    private LocalDate evaluationTime;

    /**
     * 客户方向
     */
    @ApiModelProperty(value = "客户方向")
    @NotNull(message = "客户方向不能为空")
    private BuyOrSellEnum buyOrSell;

    /**
     * 交易日期
     */
    @ApiModelProperty(value = "交易日期")
    @NotNull(message = "交易日期不能为空")
    private LocalDate tradeDate;

    /**
     * 交易日期
     */
    @ApiModelProperty(value = "如果不传就是当前时间，如果传了就用这个时间")
    private LocalTime tradeTime;

    /**
     * 到期日
     */
    @ApiModelProperty(value = "到期日")
    @NotNull(message = "到期日不能为空")
    private LocalDate maturityDate;

    /**
     * 观察列表
     */
    @ApiModelProperty(value = "观察列表")
    private List<TradeObsDateDto> tradeObsDateList;

    /**
     * 采价次数
     */
    @ApiModelProperty(value = "采价次数")
    private Integer obsNumber;
    /**
     * 成交数量
     */
    @ApiModelProperty(value = "成交数量")
    private BigDecimal tradeVolume;

    /**
     * 结算方式
     */
    @ApiModelProperty(value = "结算方式")
    private SettleTypeEnum settleType;

    /**
     * 每日数量
     */
    @ApiModelProperty(value = "每日数量")
    @DecimalMin(value = "0",message = "每日数量必须大于0")
    private BigDecimal basicQuantity;

    /**
     * 杠杆系数
     */
    @ApiModelProperty(value = "杠杆系数")
    @DecimalMin(value = "0",message = "杠杆系数必须大于0")
    private BigDecimal leverage;

    /**
     * 单位固定赔付
     */
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

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    @NotNull(message = "请选择定价客户")
    private Integer clientId;

    /**
     * 开仓交易code（用于平仓计算使用）
     */
    @ApiModelProperty(value = "开仓交易code（用于平仓计算使用）")
    private String tradeCode;

    @ApiModelProperty(value = "是否测试")
    private Boolean isTest = false;

    /**
     * 产品开始日期
     */
    @ApiModelProperty(value = "产品开始日期")
    private LocalDate productStartDate;
   //雪球相关start
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
     * 期权%单价
     */
    @ApiModelProperty(value = "期权%单价")
    private BigDecimal optionPremiumPercent;

    @ApiModelProperty(value = "期权费率是否年化")
    private  Boolean optionPremiumPercentAnnulized;

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
     * 敲入价格
     */
    @ApiModelProperty(value = "敲入价格")
    private BigDecimal knockinBarrierValue;

    /**
     * 敲入价格是否为相对水平值
     */
    @ApiModelProperty(value = "敲入价格是否为相对水平值")
    private Boolean knockinBarrierRelative;
    /**
     * 敲入价格Shift
     */
    @ApiModelProperty(value = "敲入价格Shift")
    private BigDecimal knockinBarrierShift;

    /**
     * 是否敲入
     */
    @ApiModelProperty(value = "是否敲入")
    private Boolean alreadyKnockedIn;


    /**
     * 敲入行权价格
     */
    @ApiModelProperty(value = "敲入行权价格")
    private BigDecimal strikeOnceKnockedinValue;

    /**
     * 敲入行权价格是否为相对水平值
     */
    @ApiModelProperty(value = "敲入行权价格是否为相对水平值")
    private Boolean strikeOnceKnockedinRelative;

    /**
     * 敲入行权价格Shift
     */
    @ApiModelProperty(value = "敲入行权价格Shift")
    private BigDecimal strikeOnceKnockedinShift;


    /**
     * 敲入行权价格2
     */
    @ApiModelProperty(value = "敲入行权价格2")
    private BigDecimal strike2OnceKnockedinValue;

    /**
     * 敲入行权价格2是否为相对水平值
     */
    @ApiModelProperty(value = "敲入行权价格2是否为相对水平值")
    private Boolean strike2OnceKnockedinRelative;
    /**
     * 敲入行权价格2Shift
     */
    @ApiModelProperty(value = "敲入行权价格2Shift")
    private BigDecimal strike2OnceKnockedinShift;

    //雪球相关end
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

    /**
     * 标的资产是否为权益类
     */
    @ApiModelProperty(value = "标的资产是否为权益类")
    private Boolean isEquity;

    /**
     * 股息率是否是常数
     */
    @ApiModelProperty(value = "股息率是否是常数")
    private Boolean isDividendConstant;

    /**
     * 是否敲入
     */
    @ApiModelProperty(value = "股息率是否是常数")
    private Boolean isAlreadyKnockedIn;
    /**
     * 敲入后票息是否失效
     */
    @ApiModelProperty(value = "股息率是否是常数")
    private Boolean isKnockedInEnd;

    /**
     * 敲入后客户建仓的参与率
     */
    @ApiModelProperty(value = "敲入后客户建仓的参与率")
    private double participationRatio;

    /**
     * 敲入是否头寸结算
     */
    @ApiModelProperty(value = "敲入是否头寸结算")
    private int isCashSettled;

    /**
     * 敲入后头寸是否已经完成建仓
     */
    @ApiModelProperty(value = "敲入后头寸是否已经完成建仓")
    private Boolean isSpotOpen;
}
