package org.orient.otc.yl.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.yl.enums.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
@ApiModel
public class TradeMngByYlVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;
    /**
     * 组合编号
     */
    @ApiModelProperty(value = "组合编号")
    private String combCode;
    /**
     * 顺序
     */
    @ApiModelProperty(value = "顺序")
    private Integer sort;

    /**
     * 交易员id
     */
    @ApiModelProperty(value = "交易员id")
    private Integer traderId;

    /**
     * 簿记ID
     */
    @ApiModelProperty(value = "簿记ID")
    private Integer assetId;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    /**
     * 交易日期
     */
    @ApiModelProperty(value = "交易日期")
    private LocalDate tradeDate;
    /**
     * 到期日
     */
    @ApiModelProperty(value = "到期日")
    private LocalDate maturityDate;

    /**
     * 标的合约
     */
    @ApiModelProperty(value = "标的合约")
    private String underlyingCode;

    /**
     * 入场价格
     */
    @ApiModelProperty(value = "入场价格")
    private BigDecimal entryPrice;

    /**
     * 期权组合类型
     */
    @ApiModelProperty(value = "期权组合类型")
    private OptionCombTypeEnum optionCombType;

    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型")
    private OptionTypeEnum optionType;

    /**
     * 行权方式
     */
    @ApiModelProperty(value = "行权方式")
    private ExerciseTypeEnum exerciseType;

    /**
     * 行权价格
     */
    @ApiModelProperty(value = "行权价格")
    private BigDecimal strike;

    /**
     * 客户方向
     */
    @ApiModelProperty(value = "客户方向")
    private BuyOrSellEnum buyOrSell;

    /**
     * 东证方向
     */
    @ApiModelProperty(value = "东证方向")
    private BuyOrSellEnum buyOrSellDz;

    /**
     * 看涨看跌
     */
    @ApiModelProperty(value = "看涨看跌")
    private CallOrPutEnum callOrPut;

    /**
     * 期权￥单价
     */
    @ApiModelProperty(value = "期权￥单价")
    private BigDecimal optionPremium;

    /**
     * 期权%单价
     */
    @ApiModelProperty(value = "期权%单价")
    private BigDecimal optionPremiumPercent;

    /**
     * 期权费率是否年化
     */
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
     * mid波动率
     */
    @ApiModelProperty(value = "mid波动率")
    private BigDecimal midVol;

    /**
     * 成交数量
     */
    @ApiModelProperty(value = "成交数量")
    private BigDecimal tradeVolume;

    /**
     * 成交金额
     */
    @ApiModelProperty(value = "成交金额")
    private BigDecimal totalAmount;


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
    /**
     * 存续数量
     */
    @ApiModelProperty(value = "存续数量")
    private BigDecimal availableVolume;
    /**
     * 累计盈亏
     */
    @ApiModelProperty(value = "累计盈亏")
    private BigDecimal totalProfitLoss;

    @ApiModelProperty(value = "成本")
    private BigDecimal cost;
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
    private BigDecimal basicQuantity;

    /**
     * 杠杆系数
     */
    @ApiModelProperty(value = "杠杆系数")
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
     * TTM
     */
    @ApiModelProperty(value = "TTM")
    private BigDecimal ttm;

    /**
     * 工作日
     */
    @ApiModelProperty(value = "工作日")
    private Integer workday;

    /**
     * 交易日
     */
    @ApiModelProperty(value = "交易日")
    private Integer tradingDay;

    /**
     * 公共假日
     */
    @ApiModelProperty(value = "公共假日")
    private Integer bankHoliday;

    private TradeStateEnum tradeState;

    /**
     * pv
     */
    @ApiModelProperty(value = "pv")
    private BigDecimal pv;

    /**
     * delta
     */
    @ApiModelProperty(value = "delta")
    private BigDecimal delta;

    /**
     * gamma
     */
    @ApiModelProperty(value = "gamma")
    private BigDecimal gamma;

    /**
     * vega
     */
    @ApiModelProperty(value = "vega")
    private BigDecimal vega;

    /**
     * theta
     */
    @ApiModelProperty(value = "theta")
    private BigDecimal theta;

    /**
     * rho
     */
    @ApiModelProperty(value = "rho")
    private BigDecimal rho;

    /**
     * 是否同步至镒链 0-未同步 1已同步 2同步失败
     */
    @ApiModelProperty
    private Integer isSync;


    private List<TradeObsDateVO> tradeObsDateList;
    /**
     * 保证金占用
     */
    private BigDecimal useMargin;

    /**
     * 产品开始日期
     */
    private LocalDate productStartDate;
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
