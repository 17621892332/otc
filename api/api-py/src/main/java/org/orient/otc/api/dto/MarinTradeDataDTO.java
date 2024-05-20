package org.orient.otc.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MarinTradeDataDTO {

    /**
     * 期权类型
     */
    private String optionType;

    /**
     * 交易代码
     */
    private String tradeCode;

    /**
     * 品种代码
     */
    private String varietyCode;
    /**
     * 存续数量
     */
    private BigDecimal availableVolume;
    /**
     * 买卖方向
     */
    private Integer buyOrSell;
    /**
     * 合约对应涨跌幅
     */
    private BigDecimal upDownLimit;

    /**
     * 入场价格
     */
    private BigDecimal entryPrice;
    /**
     * 当日结算价
     */
    private BigDecimal settlementPrice;
    /**
     * 当日收盘价
     */
    private BigDecimal closePrice;
    /**
     * 执行价格
     */
    private BigDecimal strike;
    /**
     * 收盘时间点
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime settlementTime;
    /**
     * 到期日期
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime maturityDate;
    /**
     * 看涨看跌
     */
    private String callOrPut;

    /**
     * 无风险利率
     */
    private BigDecimal riskFreeInterestRate;
    /**
     * 红利
     */
    private BigDecimal dividendYield;
    /**
     * 波动率曲面（最新时刻）
     */
    private List<VolatityDataDTO> volatityList;
    /**
     * 固定波动率
     */
    private BigDecimal constVol;
    /**
     * 累计期权类型
     */
    private String accumulatorType;
    /**
     * 价格列表（起始观察日至到期日15点收盘价）
     */
    private List<TradeObsDateDTO> obsDateList;

    /**
     * 每日数量（吨数）
     */
    private BigDecimal basicQuantity;
    /**
     * 结算方式
     */
    private Integer settleType;
    /**
     * 多倍系数
     */
    private BigDecimal leverage;
    /**
     * 固定赔付
     */
    private BigDecimal fixedPayment;
    /**
     * 障碍价格
     */
    private BigDecimal barrier;
    /**
     * strike ramp
     */
    private BigDecimal strikeRamp;
    /**
     * barrier ramp
     */
    private BigDecimal barrierRamp;
    /**
     * 保证金占用
     */
    private BigDecimal useMargin;

    /**
     * 名义本金
     */
    private BigDecimal availableNotionalPrincipal;

    /**
     * 敲出赔付
     */
    private BigDecimal knockoutRebate;
    /**
     * 期末杠杆系数
     */
    private BigDecimal expiryLeverage;
}
