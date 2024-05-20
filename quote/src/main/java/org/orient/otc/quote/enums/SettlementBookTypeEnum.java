package org.orient.otc.quote.enums;

import lombok.Data;
import lombok.Getter;
import org.orient.otc.api.quote.enums.OptionTypeEnum;

import java.util.Arrays;
import java.util.List;

import static org.orient.otc.api.quote.enums.OptionTypeEnum.*;

/**
 * 结算确认书模板
 * @author dzrh
 */

@Getter
public enum SettlementBookTypeEnum {
    /**
     * 亚式期权模板
     */
    asianPricer(Arrays.asList(AIAsianPricer,AIEnAsianPricer)),
    /**
     * 雪球
     */
    snowBallPricer(Arrays.asList(AISnowBallCallPricer, AISnowBallPutPricer, AIBreakEvenSnowBallCallPricer
            , AIBreakEvenSnowBallPutPricer, AILimitLossesSnowBallCallPricer, AILimitLossesSnowBallPutPricer)),
    /**
     * 香草、远期、累计
     */
    otherPricer(Arrays.asList(
            AIVanillaPricer, AIForwardPricer
            ,AICallAccPricer, AIPutAccPricer, AICallFixAccPricer, AIPutFixAccPricer
            ,AICallKOAccPricer, AIPutKOAccPricer, AICallFixKOAccPricer, AIPutFixKOAccPricer, AIEnPutKOAccPricer, AIEnCallKOAccPricer))
    ;


    private final List<OptionTypeEnum> optionTyp;

    SettlementBookTypeEnum(List<OptionTypeEnum> optionTyp) {
        this.optionTyp = optionTyp;
    }
}
