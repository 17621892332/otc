package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.common.core.config.BigDecimalFormatter;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.quote.handler.TradeObsDateListTypeHandler;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
public class TradeRiskInfo extends BaseEntity implements Serializable {

    private String id;

    /**
     * 风险日期
     */
    private LocalDate riskDate;

    /**
     * 场外or场内
     */
    private TradeRiskCacularResultSourceType tradeRiskCacularResultSourceType;

    /**
     * 期权or期货
     */
    private TradeRiskCacularResultType tradeRiskCacularResultType;

    /**
     * 组合编号
     */
    private String combCode;
    /**
     * 交易编号
     */
    private String tradeCode;

    /**
     * 关联交易编号
     */
    private String relevanceTradeCode;
    /**
     * 标的代码
     */
    private String underlyingCode;

    /**
     * 标的名称
     */
    private String underlyingName;
    /**
     * 场内标的资产码
     */
    private String exchangeUnderlyingCode;



    /**
     * 品种id
     */
    private Integer varietyId;

    /**
     * 品种代码
     */
    private String varietyCode;

    /**
     * 期权代码
     */
    private String instrumentId;

    /**
     * 合约实时行情
     */
    private BigDecimal lastPrice;

    /**
     * 期权组合类型
     */
    private OptionCombTypeEnum optionCombType;

    /**
     * 期权类型
     */
    private OptionTypeEnum optionType;

    /**
     * 看涨看跌
     */
    private CallOrPutEnum callOrPut;

    /**
     * 入场价格
     */
    private BigDecimal entryPrice;

    /**
     * 行权价格
     */
    private BigDecimal strike;

    /**
     * 东证方向
     */
    private BuyOrSellEnum buyOrSell;

    /**
     * 到期日
     */
    private LocalDate maturityDate;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 成交数量
     */
    private BigDecimal tradeVolume;

    /**
     * 期权￥单价
     */
    private BigDecimal optionPremium;
    /**
     * 成交金额
     */
    private BigDecimal totalAmount;

    /**
     * 名义本金
     */
    @ApiModelProperty(value = "名义本金")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal notionalPrincipal;



    /**
     * 存续单价
     */
    private BigDecimal availablePremium;

    /**
     * 存续名义本金
     */
    @ApiModelProperty(value = "存续名义本金")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "#,##0.00")
    private BigDecimal availableNotionalPrincipal;

    /**
     * 存续总额
     */
    private BigDecimal availableAmount;
    /**
     * 存续数量
     */
    private BigDecimal availableVolume;


    /**
     * 昨日存续总额
     */
    private BigDecimal lastTradeDayAvailableAmount;

    /**
     * 今日开仓总额
     */
    private BigDecimal todayOpenAmount;

    /**
     * 今日平仓总额
     */
    private BigDecimal todayCloseAmount;



    /**
     * 今日盈亏
     */
    private BigDecimal todayProfitLoss;

    /**
     * 累计盈亏
     */
    private BigDecimal totalProfitLoss;


    /**
     * 实现盈亏
     */
    private BigDecimal positionProfitLoss;

    /**
     * 持仓保证金
     */
    private BigDecimal margin;

    /**
     * 开仓波动率
     */
    private BigDecimal tradeVol;

    /**
     * 当前波动率
     */
    private BigDecimal nowVol;


    /**
     * 波动率覆盖
     */
    private BigDecimal riskVol;
    /**
     * 客户ID
     */
    private Integer clientId;
    /**
     * 簿记id
     */
    private Integer assetId;


    private BigDecimal accumulatedPosition;

    private BigDecimal accumulatedPayment;

    private BigDecimal accumulatedPnl;

    private BigDecimal todayAccumulatedPosition;

    private BigDecimal todayAccumulatedPayment;

    private BigDecimal todayAccumulatedPnl;

    private BigDecimal deltaLots;

    private BigDecimal delta;

    private BigDecimal deltaCash;

    private BigDecimal gammaLots;

    private BigDecimal gamma;

    private BigDecimal gammaCash;

    private BigDecimal theta;

    private BigDecimal vega;


    /**
     * 无风险利率变化对期权价值的影响
     */
    private BigDecimal rho;

    /**
     * 股息率变化对期权价值的影响
     */
    private BigDecimal dividendRho;

    private BigDecimal day1PnL;

    /**
     * 观察日列表
     */
    @TableField(typeHandler = TradeObsDateListTypeHandler.class)
    private List<TradeObsDateVO> obsDateList;
    /**
     * 结算方式
     */
    private SettleTypeEnum settleType;

    /**
     * 采价次数
     */
    private Integer obsNumber;

    /**
     * 敲出价格
     */
    private BigDecimal barrier;

    /**
     * 单日数量
     */
    private BigDecimal basicQuantity;

    /**
     * 杠杆系数
     */
    private BigDecimal leverage;

    /**
     * 固定赔付
     */
    private BigDecimal fixedPayment;

    /**
     * 敲出赔付
     */
    private BigDecimal knockoutRebate;

    /**
     * 融行账号
     */
    private String account;
    /**
     * 最后一次计算的状态
     */
    private SuccessStatusEnum status;


    /**
     * 是否敲入
     */
    private Boolean alreadyKnockedIn;

    /**
     * 红利票息
     */
    private BigDecimal bonusRateStructValue;

    /**
     * 红利票息是否年化
     */
    private Boolean bonusRateAnnulized;
    /**
     * 敲入障碍
     */
    private BigDecimal knockinBarrierValue;

    /**
     * 敲入障碍是否为相对水平值
     */
    private Boolean knockinBarrierRelative;
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
     * 是否手动维护的
     */
    private Boolean  handmade;



    /**
     * 到期倍数
     */
    @ApiModelProperty(value = "到期倍数")
    private BigDecimal expireMultiple;

    /**
     * 改变持仓方向
     * @param item 风险信息
     */
    public void changeDirection(TradeRiskInfo item){
        item.setBuyOrSell(item.getBuyOrSell() == BuyOrSellEnum.buy ? BuyOrSellEnum.sell : BuyOrSellEnum.buy);
        if (item.getOptionPremium() != null) {
            item.setOptionPremium(item.getOptionPremium().negate());
        }
        //PV的成交总额方向不用转换
//        if (item.getTotalAmount() != null) {
//            item.setTotalAmount(item.getTotalAmount().negate());
//        }
        if (item.getAvailablePremium() != null) {
            item.setAvailablePremium(item.getAvailablePremium().negate());
        }
        if (item.getAvailableAmount() != null) {
            item.setAvailableAmount(item.getAvailableAmount().negate());
        }
        if (item.getTodayProfitLoss() != null) {
            item.setTodayProfitLoss(item.getTodayProfitLoss().negate());
        }
        if (item.getTotalProfitLoss() != null) {
            item.setTotalProfitLoss(item.getTotalProfitLoss().negate());
        }
        if (item.getPositionProfitLoss() != null) {
            item.setPositionProfitLoss(item.getPositionProfitLoss().negate());
        }
        if (item.getDelta() != null) {
            item.setDelta(item.getDelta().negate());
        }
        if (item.getDeltaLots() != null) {
            item.setDeltaLots(item.getDeltaLots().negate());
        }
        if (item.getDeltaCash() != null) {
            item.setDeltaCash(item.getDeltaCash().negate());
        }
        if (item.getGamma() != null) {
            item.setGamma(item.getGamma().negate());
        }

        if (item.getGammaLots() != null) {
            item.setGammaLots(item.getGammaLots().negate());
        }
        if (item.getGammaCash() != null) {
            item.setGammaCash(item.getGammaCash().negate());
        }
        if (item.getTheta() != null) {
            item.setTheta(item.getTheta().negate());
        }
        if (item.getVega() != null) {
            item.setVega(item.getVega().negate());
        }
        if (item.getRho() != null) {
            item.setRho(item.getRho().negate());
        }
        if (item.getDividendRho() != null) {
            item.setDividendRho(item.getDividendRho().negate());
        }
        if (item.getTodayAccumulatedPosition() != null) {
            item.setTodayAccumulatedPosition(item.getTodayAccumulatedPosition().negate());
        }
        if (item.getTodayAccumulatedPayment() != null) {
            item.setTodayAccumulatedPayment(item.getTodayAccumulatedPayment().negate());
        }
        if (item.getTodayAccumulatedPnl() != null) {
            item.setTodayAccumulatedPnl(item.getTodayAccumulatedPnl().negate());
        }

        if (item.getAccumulatedPosition() != null) {
            item.setAccumulatedPosition(item.getAccumulatedPosition().negate());
        }
        if (item.getAccumulatedPayment() != null) {
            item.setAccumulatedPayment(item.getAccumulatedPayment().negate());
        }
        if (item.getAccumulatedPnl() != null) {
            item.setAccumulatedPnl(item.getAccumulatedPnl().negate());
        }
    }

    /**
     * 设置精度
     * @param item 风险数据
     */
    public void setScale(TradeRiskInfo item){
        if (item.getLastPrice()!=null){
            item.setLastPrice(item.getLastPrice().setScale(2, RoundingMode.HALF_UP));
        }
        if (item.getTotalAmount()!=null){
            item.setTotalAmount(item.getTotalAmount().setScale(2,RoundingMode.HALF_UP));
        }
        if (item.getNotionalPrincipal()!=null){
            item.setNotionalPrincipal(item.getNotionalPrincipal().setScale(2,RoundingMode.HALF_UP));
        }
        if (item.getAvailableAmount()!=null){
            item.setAvailableAmount(item.getAvailableAmount().setScale(2,RoundingMode.HALF_UP));
        }
        if (item.getTotalProfitLoss()!=null){
            item.setTotalProfitLoss(item.getTotalProfitLoss().setScale(2,RoundingMode.HALF_UP));
        }
        if (item.getMargin()!=null){
            item.setMargin(item.getMargin().setScale(2,RoundingMode.HALF_UP));
        }
    }
}
