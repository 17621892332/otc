package org.orient.otc.common.jni.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 隐含波动率
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIBlackImpliedVolRequest {
    /**
     * 期权合约规定的行权方式(传入为{"european",  "american"})
     */
    String exerciseType;

    /**
     * 期权类型
     */
    String optionType;

    /**
     * 标的合约价格
     */
    double underlyingPrice;

    /**
     * 执行价
     */
    double strike;

    /**
     * 定价时点的时间戳
     */
    long evaluationTime;

    /**
     * 到期时间点的时间戳
     */
    long expiryTime;

    /**
     * 无风险利率
     */
    double riskFreeInterestRate;

    /**
     * 股息率
     */
    double dividendYield;

    /**
     * 期权现值
     */
    double pv;

}
