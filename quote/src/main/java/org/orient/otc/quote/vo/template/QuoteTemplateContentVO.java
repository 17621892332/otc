package org.orient.otc.quote.vo.template;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.quote.handler.TradeObsDateListTypeHandler;

import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 定价计算模板
 */
@Data
@ApiModel
public class QuoteTemplateContentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "模板id")
    private Integer templateId;

    @ApiModelProperty(value = "顺序")
    private Integer sort;

    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

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

    /**
     * mid分红率
     */
    @ApiModelProperty(value = "mid分红率")
    private BigDecimal midDividendYield;

    @ApiModelProperty(value = "成交数量")
    private BigDecimal tradeVolume;

    @ApiModelProperty(value = "成交金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "Day1 PnL")
    private BigDecimal day1PnL;

    @ApiModelProperty(value = "TTM")
    private BigDecimal ttm;

    @ApiModelProperty(value = "工作日")
    private Integer workday;

    @ApiModelProperty(value = "交易日")
    private Integer tradingDay;

    @ApiModelProperty(value = "公共假日")
    private Integer bankHoliday;

    @ApiModelProperty(value = "结算方式")
    private SettleTypeEnum settleType;
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
    private BigDecimal barrier;
    @ApiModelProperty(value = "执行价斜坡")
    private BigDecimal strikeRamp;
    @ApiModelProperty(value = "障碍价斜坡")
    private BigDecimal barrierRamp;

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

    @ApiModelProperty(value = "观望日期列表")
    @TableField(typeHandler = TradeObsDateListTypeHandler.class)
    List<TradeObsDateVO> tradeObsDateList;

    /**
     * 名义本金
     */
    @ApiModelProperty(value = "名义本金")
    private BigDecimal notionalPrincipal;
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
     * 敲出障碍
     */
    private BigDecimal knockinBarrierValue;

    /**
     * 敲出障碍是否为相对水平值
     */
    private Boolean knockinBarrierRelative;
    /**
     * 敲出障碍Shift
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
     * 障碍是否为相对水平值
     */
    @ApiModelProperty(value = "障碍是否为相对水平值")
    private Boolean barrierRelative;
    /**
     * 障碍Shift
     */
    @ApiModelProperty(value = "障碍Shift")
    private String barrierShift;

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
     * 到期Shift
     */
    private String maturityDateShift;
    /**
     * 行权Shift
     */
    private String strikeShift;
}
