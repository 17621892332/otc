package org.orient.otc.quote.vo.daily;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.CallOrPutEnum;
import org.orient.otc.api.quote.enums.OptionCombTypeEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.quote.handler.TradeObsDateListTypeHandler;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
public class PositionDailyVO  implements Serializable {

    /**
     * 交易ID
     */
    private Integer id;

    /**
     * 客户ID
     */
    private Integer clientId;

    /**
     * 交易编号
     */
    private String tradeCode;

    /**
     * 交易确认书编号
     */
    private String tradeConfirmCode;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;



    /**
     * 东证方向
     */
    private BuyOrSellEnum buyOrSell;

    /**
     * 标的代码
     */
    private String underlyingCode;

    /**
     * 期权类型
     */
    private OptionTypeEnum optionType;

    /**
     * 期权组合类型
     */
    private OptionCombTypeEnum optionCombType;

    /**
     * 结构类型
     */
    private String structureType;
    /**
     * 看涨看跌
     */
    private CallOrPutEnum callOrPut;
    /**
     * 持仓保证金
     */
    private BigDecimal margin;
    /**
     * 初始保证金
     */
    private BigDecimal initMargin;

    /**
     * 保证金占用
     */
    private BigDecimal useMargin;

    /**
     * 入场价格
     */
    private BigDecimal entryPrice;

    /**
     * 行权价格
     */
    private BigDecimal strike;

    /**
     * 成交金额
     */
    private BigDecimal totalAmount;

    /**
     * 成交数量
     */
    private BigDecimal tradeVolume;

    /**
     * 期权￥单价
     */
    private BigDecimal optionPremium;

    /**
     * 期权费的费率
     */
    private BigDecimal optionPremiumPercent;

    /**
     * 名义本金
     */
    private BigDecimal notionalPrincipal;
    /**
     * 到期日期
     */
    private LocalDate maturityDate;

    /**
     * 敲入障碍
     */
    private BigDecimal knockinBarrierValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    private Boolean knockinBarrierRelative;

    /**
     * 红利票息
     */
    private BigDecimal bonusRateStructValue;

    /**
     * 红利票息是否年化
     */
    private Boolean bonusRateAnnulized;

    /**
     * 敲入障碍Shift
     */
    private BigDecimal knockinBarrierShift;


    /**
     * 敲入行权价格
     */
    private BigDecimal strikeOnceKnockedinValue;

    /**
     * 敲入行权价格是否为相对水平值
     */
    private Boolean strikeOnceKnockedinRelative;

    /**
     * 敲入行权价格Shift
     */
    private BigDecimal strikeOnceKnockedinShift;


    /**
     * 敲入行权价格2
     */
    private BigDecimal strike2OnceKnockedinValue;

    /**
     * 敲入行权价格2是否为相对水平值
     */
    private Boolean strike2OnceKnockedinRelative;

    /**
     * 敲入行权价格2Shift
     */
    private BigDecimal strike2OnceKnockedinShift;

    /**
     * 观察日列表
     */
    @TableField(typeHandler = TradeObsDateListTypeHandler.class)
    private List<TradeObsDateVO> obsDateList;

    //风险部分数据
    /**
     * 风险日期
     */
    private LocalDate riskDate;

    /**
     * 是否敲入
     */
    private Boolean alreadyKnockedIn;

    /**
     * 合约实时行情
     */
    private BigDecimal lastPrice;

    /**
     * 存续数量
     */
    private BigDecimal availableVolume;

    /**
     * 存续总额
     */
    private BigDecimal availableAmount;

    /**
     * 存续单价
     */
    private BigDecimal availablePremium;

    /**
     * 存续名义本金
     */
    private BigDecimal availableNotionalPrincipal;

    /**
     * 当前波动率
     */
    private BigDecimal nowVol;

    private BigDecimal delta;

    private BigDecimal gamma;

    private BigDecimal theta;

    private BigDecimal vega;

    /**
     * 无风险利率变化对期权价值的影响
     */
    private BigDecimal rho;

    private BigDecimal deltaCash;

    private BigDecimal gammaLots;

    private BigDecimal gammaCash;

}
