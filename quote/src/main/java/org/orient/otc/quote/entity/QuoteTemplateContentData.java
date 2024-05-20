package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 交易模板
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class QuoteTemplateContentData  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 客户ID
     */
    private Integer clientId;

    /**
     * 交易日期
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate tradeDate;

    /**
     * 产品开始日期
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate productStartDate;

    /**
     * 到期日
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate maturityDate;

    /**
     * 标的合约
     */
    private String underlyingCode;

    /**
     * 入场价格
     */
    private BigDecimal entryPrice;

    /**
     * 期权组合类型
     */
    private OptionCombTypeEnum optionCombType;

    /**
     * 期权类型
     */
    private OptionTypeEnum optionType;

    /**
     * 行权方式
     */
    private ExerciseTypeEnum exerciseType;


    /**
     * 保底封顶
     */
    private CeilFloorEnum ceilFloor;

    /**
     * 行权价格
     */
    private BigDecimal strike;

    /**
     * 行权价格2
     */
    private BigDecimal strike2;
    /**
     * 增强价格
     */
    private BigDecimal enhancedStrike;

    /**
     * 折扣率
     */
    private BigDecimal discountRate;

    /**
     * 客户方向
     */
    private BuyOrSellEnum buyOrSell;

    /**
     * 看涨看跌
     */
    private CallOrPutEnum callOrPut;

    /**
     * 期权￥单价
     */
    private BigDecimal optionPremium;

    /**
     * 期权%单价
     */
    private BigDecimal optionPremiumPercent;

    /**
     * 期权费率是否年化
     */
    @ApiModelProperty(value = "期权费率是否年化")
    private  Boolean optionPremiumPercentAnnulized;

    /**
     * 保证金￥单价
     */
    private BigDecimal margin;

    /**
     * 交易波动率
     */
    private BigDecimal tradeVol;


    /**
     * 成交分红率
     */
    private BigDecimal tradeDividendYield;

    /**
     * mid波动率
     */
    private BigDecimal midVol;

    /**
     * mid分红率
     */
    private BigDecimal midDividendYield;

    /**
     * 成交数量
     */
    private BigDecimal tradeVolume;

    /**
     * 成交金额
     */
    private BigDecimal totalAmount;

    /**
     * 名义本金
     */
    private BigDecimal notionalPrincipal;

    /**
     * Day1 PnL
     */
    private BigDecimal day1PnL;

    /**
     * TTM
     */
    private BigDecimal ttm;

    /**
     * 工作日
     */
    private Integer workday;

    /**
     * 交易日
     */
    private Integer tradingDay;

    /**
     * 公共假日
     */
    private Integer bankHoliday;

    /**
     * 结算方式
     */
    private SettleTypeEnum settleType;

    /**
     * 起始观察日期
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startObsDate;

    /**
     * 采价次数
     */
    private Integer obsNumber;

    /**
     * 每日数量
     */
    private BigDecimal basicQuantity;

    /**
     * 杠杆系数
     */
    private BigDecimal leverage;

    /**
     * 单位固定赔付
     */
    private BigDecimal fixedPayment;

    /**
     * 敲出价格
     */
    private BigDecimal barrier;

    /**
     * 执行价斜坡
     */
    private BigDecimal strikeRamp;

    /**
     * 障碍价斜坡
     */
    private BigDecimal barrierRamp;

    private BigDecimal pv;

    private BigDecimal delta;

    private BigDecimal gamma;

    private BigDecimal vega;

    private BigDecimal theta;

    private BigDecimal rho;

    private BigDecimal dividendRho;

    /**
     * 观望日期列表
     */
    List<TradeObsDateVO> tradeObsDateList;

    /**
     * 敲出赔付
     */
    private BigDecimal knockoutRebate;
    /**
     * 到期倍数
     */
    private BigDecimal expireMultiple;

    /**
     * 到期Shift
     */
    private String maturityDateShift;
    /**
     * 行权Shift
     */
    private String strikeShift;
    /**
     * 障碍Shift
     */
    private String barrierShift;
}
