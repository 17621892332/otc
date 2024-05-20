package org.orient.otc.quote.vo.daily;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class TradeMngByDailyVO implements Serializable {

    /**
     * 交易代码
     */
    private String tradeCode;

    /**
     * 客户ID
     */
    private Integer clientId;

    /**
     * 交易确认书编号
     */
    @ApiModelProperty(value = "交易确认书编号")
    private String tradeConfirmCode;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 到期日期
     */
    private LocalDate maturityDate;

    /**
     * 初始保证金
     */
    private BigDecimal initMargin;

    /**
     * 保证金占用
     */
    private BigDecimal useMargin;

    /**
     * 客户方向
     */
    private BuyOrSellEnum buyOrSell;

    /**
     * 标的合约
     */
    private String underlyingCode;

    /**
     * 期权类型
     */
    private OptionTypeEnum optionType;

    /**
     * 自定义结构类型
     */
    private String structureType;
    /**
     * 行权方式
     */
    private ExerciseTypeEnum exerciseType;

    /**
     * 看涨看跌
     */
    private CallOrPutEnum callOrPut;

    /**
     * 期权类型
     */
    private OptionCombTypeEnum optionCombType;


    /**
     * 入场价格
     */
    private BigDecimal entryPrice;

    /**
     * 行权价格
     */
    private BigDecimal strike;

    /**
     * 成交数量
     */
    private BigDecimal tradeVolume;

    /**
     * 名义本金
     */
    private BigDecimal notionalPrincipal;

    /**
     * 成交金额
     */
    private BigDecimal totalAmount;

    /**
     * 交易状态
     */
    private TradeStateEnum tradeState;
    /**
     * 平仓ID
     */
    private Integer closeId;

    @ApiModelProperty(value = "结算确认书编号")
    private String settlementConfirmCode;

    /**
     * 平仓日期
     */
    private LocalDate closeDate;

    /**
     * 平仓入场价格
     */
    private BigDecimal closeEntryPrice;

    /**
     * 平仓数量
     */
    private BigDecimal closeVolume;

    /**
     * 平仓金额
     */
    private BigDecimal closeTotalAmount;

    /**
     * 平仓名义本金
     */
    private BigDecimal closeNotionalPrincipal;

    private BigDecimal delta;

    private BigDecimal closeDelta;

}
