package org.orient.otc.yl.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dzrh
 */
@Getter
public enum OptionTypeEnum {
    /**
     * 香草期权
     */
    AIVanillaPricer("AIVanillaPricer","香草期权"),
    /**
     * 远期
     */
    AIForwardPricer("AIForwardPricer","远期"),
    /**
     * 亚式期权
     */
    AIAsianPricer("AIAsianPricer","亚式期权"),
    /**
     * 增强亚式
     */
    AIEnAsianPricer("AIEnAsianPricer","亚式期权"),

    /**
     * 累购期权
     */
    AICallAccPricer("AICallAccPricer","累购期权"),
    /**
     * 累沽期权
     */
    AIPutAccPricer("AIPutAccPricer","累沽期权"),
    /**
     * 固定赔付累购
     */
    AICallFixAccPricer("AICallFixAccPricer","固定赔付累购"),
    /**
     * 固定赔付累沽
     */
    AIPutFixAccPricer("AIPutFixAccPricer","固定赔付累沽"),
    /**
     * 雪球看涨
     */
    AISnowBallCallPricer("AISnowBallPricer","雪球期权"),
    /**
     * 雪球看跌
     */
    AISnowBallPutPricer("AISnowBallPricer","雪球期权"),
    /**
     * 限亏雪球看涨
     */
    AILimitLossesSnowBallCallPricer("AISnowBallPricer","雪球期权"),
    /**
     * 限亏雪球看跌
     */
    AILimitLossesSnowBallPutPricer("AISnowBallPricer","雪球期权"),
    /**
     * 保本雪球看跌
     */
    AIBreakEvenSnowBallPutPricer("AISnowBallPricer","雪球期权"),
    /**
     * 保本雪球看涨
     */
    AIBreakEvenSnowBallCallPricer("AISnowBallPricer","雪球期权"),


    /**
     * 熔断累购期权
     */
    AICallKOAccPricer("AICallKOAccPricer","熔断累购期权"),
    /**
     * 熔断累沽期权
     */
    AIPutKOAccPricer("AIPutKOAccPricer","熔断累沽期权"),


    /**
     * 熔断增强累购
     */
    AIEnCallKOAccPricer("AIEnCallKOAccPricer", "熔断增强累购"),
    /**
     * 熔断增强累沽
     */
    AIEnPutKOAccPricer("AIEnPutKOAccPricer", "熔断增强累沽"),
    /**
     * 熔断固赔累购
     */
    AICallFixKOAccPricer("AICallFixKOAccPricer","熔断固赔累购"),
    /**
     * 熔断固赔累沽
     */
    AIPutFixKOAccPricer("AIPutFixKOAccPricer","熔断固赔累沽"),
    /**
     * 自定义交易
     */
    AICustomPricer("AICustomPricer","自定义交易")
    ;
    private final String key;
    private final String desc;

    OptionTypeEnum(String key,String desc) {
        this.key=key;
        this.desc = desc;
    }
    /**
     * 获取枚举
     * @param key 枚举key
     * @return 枚举
     */
    public static OptionTypeEnum getTradeTypeByKey(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (OptionTypeEnum enums : OptionTypeEnum.values()){
            if (enums.getKey().equals(key)){
                return enums;
            }
        }
        return null;
    }

    /**
     * 获取枚举的描述
     * @param key 枚举key
     * @return 描述
     */
    public static OptionTypeEnum getTradeTypeByDesc(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (OptionTypeEnum enums : OptionTypeEnum.values()){
            if (enums.getDesc().equals(key)){
                return enums;
            }
        }
        return null;
    }
}
