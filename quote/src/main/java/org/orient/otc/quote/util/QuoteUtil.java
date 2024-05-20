package org.orient.otc.quote.util;

import org.orient.otc.api.quote.enums.CallOrPutEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.common.jni.enums.AccumulatorTypeEnum;
import org.orient.otc.quote.dto.quote.QuoteMakeUpTotalDTO;
import org.orient.otc.quote.vo.quote.QuoteMakeUpTotalVo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 定价计算工具类
 */
@Component
public class QuoteUtil {
    /**
     * 组合求和
     * @param quoteMakeUpTotalDTOS 组合列表
     * @return 求和结果
     */
    public static QuoteMakeUpTotalVo getMakeUpTotal(List<QuoteMakeUpTotalDTO> quoteMakeUpTotalDTOS){
        QuoteMakeUpTotalVo quoteMakeUpTotalVo = new QuoteMakeUpTotalVo();
        BigDecimal pv = BigDecimal.valueOf(0.0);
        BigDecimal delta = BigDecimal.valueOf(0.0);
        BigDecimal gamma = BigDecimal.valueOf(0.0);
        BigDecimal vega = BigDecimal.valueOf(0.0);
        BigDecimal theta = BigDecimal.valueOf(0.0);
        BigDecimal rho = BigDecimal.valueOf(0.0);
        BigDecimal optionPremium = BigDecimal.valueOf(0.0);
        BigDecimal optionPremiumPercent = BigDecimal.valueOf(0.0);
        BigDecimal day1PnL = BigDecimal.valueOf(0.0);
        BigDecimal margin = BigDecimal.valueOf(0.0);
        BigDecimal totalAmount = BigDecimal.valueOf(0.0);
        BigDecimal profitLoss = BigDecimal.valueOf(0.0);
        for(QuoteMakeUpTotalDTO quoteMakeUpTotalDto : quoteMakeUpTotalDTOS){
            if(quoteMakeUpTotalDto.getPv() == null){
                pv = null;
            }else {
                if(pv != null) {
                    pv = pv.add(quoteMakeUpTotalDto.getPv());
                }
            }
            if(quoteMakeUpTotalDto.getDelta() == null){
                delta = null;
            }else {
                if(delta != null) {
                    delta = delta.add(quoteMakeUpTotalDto.getDelta());
                }
            }
            if(quoteMakeUpTotalDto.getGamma() == null){
                gamma = null;
            }else {
                if(gamma != null) {
                    gamma = gamma.add(quoteMakeUpTotalDto.getGamma());
                }
            }
            if(quoteMakeUpTotalDto.getVega() == null){
                vega = null;
            }else {
                if(vega != null) {
                    vega = vega.add(quoteMakeUpTotalDto.getVega());
                }
            }
            if(quoteMakeUpTotalDto.getTheta() == null){
                theta = null;
            }else {
                if(theta != null) {
                    theta = theta.add(quoteMakeUpTotalDto.getTheta());
                }
            }
            if(quoteMakeUpTotalDto.getRho() == null){
                rho = null;
            }else {
                if(rho != null) {
                    rho = rho.add(quoteMakeUpTotalDto.getRho());
                }
            }
            if(quoteMakeUpTotalDto.getOptionPremium() == null){
                optionPremium = null;
            }else {
                if(optionPremium != null) {
                    optionPremium = optionPremium.add(quoteMakeUpTotalDto.getOptionPremium());
                }
            }
            if(quoteMakeUpTotalDto.getOptionPremiumPercent() == null){
                optionPremiumPercent = null;
            }else {
                if(optionPremiumPercent != null) {
                    optionPremiumPercent = optionPremiumPercent.add(quoteMakeUpTotalDto.getOptionPremiumPercent());
                }
            }
            if(quoteMakeUpTotalDto.getDay1PnL() == null){
                day1PnL = null;
            }else {
                if(day1PnL != null) {
                    day1PnL = day1PnL.add(quoteMakeUpTotalDto.getDay1PnL());
                }
            }
            if(quoteMakeUpTotalDto.getMargin() == null){
                margin = null;
            }else {
                if(margin != null) {
                    margin = margin.add(quoteMakeUpTotalDto.getMargin());
                }
            }
            if(quoteMakeUpTotalDto.getTotalAmount() == null){
                totalAmount = null;
            }else {
                if(totalAmount != null) {
                    totalAmount = totalAmount.add(quoteMakeUpTotalDto.getTotalAmount());
                }
            }
            if(quoteMakeUpTotalDto.getProfitLoss() == null){
                profitLoss = null;
            }else {
                if(profitLoss != null) {
                    profitLoss = profitLoss.add(quoteMakeUpTotalDto.getProfitLoss());
                }
            }
        }
        quoteMakeUpTotalVo.setPv(pv);
        quoteMakeUpTotalVo.setDelta(delta);
        quoteMakeUpTotalVo.setGamma(gamma);
        quoteMakeUpTotalVo.setVega(vega);
        quoteMakeUpTotalVo.setTheta(theta);
        quoteMakeUpTotalVo.setRho(rho);
        quoteMakeUpTotalVo.setOptionPremium(optionPremium);
        quoteMakeUpTotalVo.setOptionPremiumPercent(optionPremiumPercent);
        quoteMakeUpTotalVo.setDay1PnL(day1PnL);
        quoteMakeUpTotalVo.setMargin(margin);
        quoteMakeUpTotalVo.setTotalAmount(totalAmount);
        quoteMakeUpTotalVo.setProfitLoss(profitLoss);
        return quoteMakeUpTotalVo;
    }

    /**
     * 保留两位小数
     * @param a 数字
     * @return 字符串
     */
    public static String keepTwoDecimalPlaces(BigDecimal a){
        if(a == null){
            return null;
        }
        DecimalFormat df1 = new DecimalFormat("0.00");
        return df1.format(a);
    }

    /**
     * 保留三位小数
     * @param a 数字
     * @return 字符串
     */
    public static String keepThreeDecimalPlaces(BigDecimal a){
        if(a == null){
            return null;
        }
        DecimalFormat df1 = new DecimalFormat("0.000");
        return df1.format(a);
    }
    /**
     * 保留四位小数
     * @param a 数字
     * @return 字符串
     */
    public static String keepFourDecimalPlaces(BigDecimal a){
        if(a == null){
            return null;
        }
        DecimalFormat df1 = new DecimalFormat("0.0000");
        return df1.format(a);
    }

    /**
     * 获取累计期权计算入参
     * @param optionTypeEnum 期权类型
     * @return 计算参数
     */
    public static String getAccumulatorType(OptionTypeEnum optionTypeEnum) {
        String accumulatorType = null;
        switch (optionTypeEnum) {
            case AICallAccPricer:
            case AICallKOAccPricer:
                accumulatorType = AccumulatorTypeEnum.acccall.name();
                break;
            case AIPutAccPricer:
            case AIPutKOAccPricer:
                accumulatorType = AccumulatorTypeEnum.accput.name();
                break;
            case AICallFixAccPricer:
            case AICallFixKOAccPricer:
                accumulatorType = AccumulatorTypeEnum.fpcall.name();
                break;
            case AIPutFixAccPricer:
            case AIPutFixKOAccPricer:
                accumulatorType = AccumulatorTypeEnum.fpput.name();
                break;
            case AIEnCallKOAccPricer:
                accumulatorType=AccumulatorTypeEnum.acccallplus.name();
                break;
            case AIEnPutKOAccPricer:
                accumulatorType=AccumulatorTypeEnum.accputplus.name();
                break;
        }
        return accumulatorType;
    }

    /**
     * 获取期权看涨看跌类型
     * @param optionTypeEnum 期权类型
     * @return 看涨或看跌
     */
    public static String getCallOrPut(OptionTypeEnum optionTypeEnum){
        if (optionTypeEnum == OptionTypeEnum.AISnowBallPutPricer
                || optionTypeEnum == OptionTypeEnum.AIBreakEvenSnowBallPutPricer
                || optionTypeEnum == OptionTypeEnum.AILimitLossesSnowBallPutPricer) {
            return CallOrPutEnum.put.name();
        } else {
            return  CallOrPutEnum.call.name();
        }
    }
}
