package org.orient.otc.api.quote.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author dzrh
 */

@Getter
public enum OptionTypeEnum {

    /**
     * 香草期权
     */
    AIVanillaPricer("VA", "香草期权", "香草期权"),

    /**
     * 远期
     */
    AIForwardPricer("O", "远期", "远期"),

    /**
     * 亚式期权
     */
    AIAsianPricer("AS", "欧式亚式", "亚式期权"),

    /**
     * 增强亚式
     */
    AIEnAsianPricer("AS", "增强亚式", "增强亚式期权"),

    /**
     * 累购期权
     */
    AICallAccPricer("RA", "累计期权", "累购期权"),

    /**
     * 累沽期权
     */
    AIPutAccPricer("RA", "累计期权", "累沽期权"),

    /**
     * 固定赔付累购
     */
    AICallFixAccPricer("RA", "累计期权", "固定赔付累购期权"),

    /**
     * 固定赔付累沽
     */
    AIPutFixAccPricer("RA", "累计期权", "固定赔付累沽期权"),

    /**
     * 雪球看涨
     */
    AISnowBallCallPricer("SA", "雪球", "雪球看涨期权"),

    /**
     * 雪球看跌
     */
    AISnowBallPutPricer("SA", "雪球", "雪球看跌期权"),

    /**
     * 限亏雪球看涨
     */
    AILimitLossesSnowBallCallPricer("SA", "雪球", "限亏雪球看涨期权"),

    /**
     * 限亏雪球看跌
     */
    AILimitLossesSnowBallPutPricer("SA", "雪球", "限亏雪球看跌期权"),

    /**
     * 保本雪球看跌
     */
    AIBreakEvenSnowBallPutPricer("SA", "雪球", "保本雪球看跌期权"),

    /**
     * 保本雪球看涨
     */
    AIBreakEvenSnowBallCallPricer("SA", "雪球", "保本雪球看涨期权"),

    /**
     * 熔断增强累购
     */
    AIEnCallKOAccPricer("RA", "熔断累计", "熔断增强累购期权"),

    /**
     * 熔断增强累沽
     */
    AIEnPutKOAccPricer("RA", "熔断累计", "熔断增强累沽期权"),

    /**
     * 熔断累购
     */
    AICallKOAccPricer("RA", "熔断累计", "熔断累购期权"),

    /**
     * 熔断累沽
     */
    AIPutKOAccPricer("RA", "熔断累计", "熔断累沽期权"),

    /**
     * 熔断固赔累购
     */
    AICallFixKOAccPricer("RA", "熔断累计", "熔断固赔累购期权"),

    /**
     * 熔断固赔累沽
     */
    AIPutFixKOAccPricer("RA", "熔断累计", "熔断固赔累沽期权"),

    /**
     * 自定义交易
     */
    AICustomPricer("O","自定义交易","自定义交易"),

    /**
     * 保险亚式期权
     */
    AIInsuranceAsianPricer("","保险亚式","保险亚式期权")
    ;

    private final String key;
    private final String type;
    private final String desc;

    OptionTypeEnum(String key, String type, String desc) {
        this.key = key;
        this.type = type;
        this.desc = desc;
    }

    /**
     * 是否允许使用混合方式结算
     * @param typeEnum 期权类型
     * @return true 允许 false 不允许
     */
    public static Boolean checkHaveMix(OptionTypeEnum typeEnum) {
        return typeEnum == AICallAccPricer || typeEnum == AICallKOAccPricer
                || typeEnum == AIPutAccPricer || typeEnum == AIPutKOAccPricer
                || typeEnum == AIEnPutKOAccPricer || typeEnum == AIEnCallKOAccPricer;
    }

    /**
     * 包含观察日的期权类型
     * @return 期权类型
     */
    public static List<OptionTypeEnum> getHaveObsType() {
        return Arrays.asList(
                AIEnAsianPricer, AIAsianPricer
                ,AICallAccPricer, AIPutAccPricer, AICallFixAccPricer, AIPutFixAccPricer
                ,AICallKOAccPricer, AIPutKOAccPricer, AICallFixKOAccPricer, AIPutFixKOAccPricer, AIEnPutKOAccPricer, AIEnCallKOAccPricer
                ,AISnowBallCallPricer, AISnowBallPutPricer, AIBreakEvenSnowBallCallPricer, AIBreakEvenSnowBallPutPricer, AILimitLossesSnowBallCallPricer, AILimitLossesSnowBallPutPricer
        );
    }

    /**
     * 系统里面所有累计期权
     * @return 期权类型
     */
    public static List<OptionTypeEnum> getAccOption() {
        return Arrays.asList(AICallAccPricer, AIPutAccPricer, AICallFixAccPricer, AIPutFixAccPricer
                ,AICallKOAccPricer, AIPutKOAccPricer, AICallFixKOAccPricer, AIPutFixKOAccPricer
                , AIEnPutKOAccPricer, AIEnCallKOAccPricer);
    }
    /**
     * 普通累计期权
     * @return 普通累计期权
     */
    public static List<OptionTypeEnum> getOrdinaryAccOptionType(){
        return Arrays.asList(AICallAccPricer, AIPutAccPricer, AICallFixAccPricer, AIPutFixAccPricer);
    }
    /**
     * 熔断累计期权类型
     * @return 熔断累计期权类型
     */
    public static List<OptionTypeEnum> getKOOptionType(){
        return Arrays.asList(AICallKOAccPricer, AIPutKOAccPricer, AICallFixKOAccPricer, AIPutFixKOAccPricer, AIEnPutKOAccPricer, AIEnCallKOAccPricer);
    }
    /**
     * 获取普通熔断累计期权类型
     * @return 普通累计
     */
    public static List<OptionTypeEnum> getOrdinaryKOOptionType(){
        return Arrays.asList(AICallKOAccPricer, AIPutKOAccPricer, AICallFixKOAccPricer, AIPutFixKOAccPricer);
    }
    /**
     * 获取亚式期权类型
     * @return 亚式期权
     */
    public static List<OptionTypeEnum> getAsianOptionType(){
        return Arrays.asList(AIEnAsianPricer, AIAsianPricer);
    }

    /**
     * 获取雪球期权类型
     * @return 雪球期权
     */
    public static List<OptionTypeEnum> getSnowBall() {
        return Arrays.asList(AISnowBallCallPricer, AISnowBallPutPricer, AIBreakEvenSnowBallCallPricer, AIBreakEvenSnowBallPutPricer, AILimitLossesSnowBallCallPricer, AILimitLossesSnowBallPutPricer);
    }
    /**
     * 需要生成远期的期权类型
     * @return 期权类型
     */
    public static List<OptionTypeEnum> getNeedGenerateForwardOptionType(){
        return Arrays.asList(AICallAccPricer, AIPutAccPricer, AICallFixAccPricer, AIPutFixAccPricer
                ,AICallKOAccPricer, AIPutKOAccPricer, AICallFixKOAccPricer, AIPutFixKOAccPricer
                , AIEnPutKOAccPricer, AIEnCallKOAccPricer
                ,AIEnAsianPricer);
    }
    /**
     * 固定赔付的累计期权
     * @return 固定赔付的累计期权
     */
    public static List<OptionTypeEnum> getFixOptionType(){
        return Arrays.asList(AICallFixAccPricer, AIPutFixAccPricer,AICallFixKOAccPricer, AIPutFixKOAccPricer);
    }
    /**
     * 获取需要敲出的期权类型
     * @return 期权类型列表
     */
    public static List<OptionTypeEnum> getHaveKnockOut() {
        return Arrays.asList(AISnowBallCallPricer, AISnowBallPutPricer, AIBreakEvenSnowBallCallPricer, AIBreakEvenSnowBallPutPricer, AILimitLossesSnowBallCallPricer, AILimitLossesSnowBallPutPricer
                , AICallKOAccPricer, AIPutKOAccPricer, AICallFixKOAccPricer, AIPutFixKOAccPricer, AIEnPutKOAccPricer, AIEnCallKOAccPricer);
    }

    /**
     * 获取看跌期权
     * @return 期权类型列表
     */
    public static  List<OptionTypeEnum> getPutOptionType(){
        return Arrays.asList( AISnowBallPutPricer, AIBreakEvenSnowBallPutPricer, AILimitLossesSnowBallPutPricer
                ,AIPutAccPricer,AIPutFixAccPricer, AIPutKOAccPricer, AIPutFixKOAccPricer, AIEnPutKOAccPricer);
    }
    /**
     * 获取看涨期权
     * @return 期权类型列表
     */
    public static  List<OptionTypeEnum> getCallOptionType(){
        return Arrays.asList(AISnowBallCallPricer, AIBreakEvenSnowBallCallPricer, AILimitLossesSnowBallCallPricer
                , AICallKOAccPricer, AICallFixKOAccPricer, AIEnCallKOAccPricer);
    }

    /**
     * 获取看跌敲出期权
     * @return 期权类型列表
     */
    public static  List<OptionTypeEnum> getPutKnockOut(){
        return Arrays.asList( AISnowBallPutPricer, AIBreakEvenSnowBallPutPricer, AILimitLossesSnowBallPutPricer
                , AIPutKOAccPricer, AIPutFixKOAccPricer, AIEnPutKOAccPricer);
    }
    /**
     * 获取看涨敲出期权
     * @return 期权类型列表
     */
    public static  List<OptionTypeEnum> getCallKnockOut(){
        return Arrays.asList(AISnowBallCallPricer, AIBreakEvenSnowBallCallPricer, AILimitLossesSnowBallCallPricer
                , AICallKOAccPricer, AICallFixKOAccPricer, AIEnCallKOAccPricer);
    }
    /**
     * 获取不需要单位的期权类型
     * @return 雪球期权
     */
    public static List<OptionTypeEnum> getNotNeedUnit() {
        return Arrays.asList(AIVanillaPricer,AIForwardPricer,AISnowBallCallPricer, AISnowBallPutPricer, AIBreakEvenSnowBallCallPricer, AIBreakEvenSnowBallPutPricer, AILimitLossesSnowBallCallPricer, AILimitLossesSnowBallPutPricer);
    }

    /**
     * 报送时看涨类型的期权
     * @return 报送时看涨类型
     */
    public static  List<OptionTypeEnum> getDailyCall(){
        return Arrays.asList(AICallAccPricer,AICallFixAccPricer
                ,AICallKOAccPricer,AICallFixKOAccPricer,AIEnCallKOAccPricer
                , AISnowBallCallPricer, AILimitLossesSnowBallPutPricer);
    }

    /**
     * 报送时看跌类型的期权
     * @return 报送时看涨类型
     */
    public static  List<OptionTypeEnum> getDailyPut(){
        return Arrays.asList(AIPutAccPricer,AIPutFixAccPricer
                ,AIPutKOAccPricer,AIPutFixKOAccPricer,AIEnPutKOAccPricer
                , AISnowBallPutPricer, AILimitLossesSnowBallCallPricer);
    }
    /**
     * 报送时看跌类型的期权
     * @return 报送时看涨类型
     */
    public static  List<OptionTypeEnum> getChooser(){
        return Arrays.asList(AIBreakEvenSnowBallCallPricer, AIBreakEvenSnowBallPutPricer);
    }
    /**
     * 通过name获取枚举对象
     * @param name 枚举名称
     * @return 枚举对象
     */
    public static OptionTypeEnum getTradeTypeByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (OptionTypeEnum enums : OptionTypeEnum.values()) {
            if (enums.name().equals(name)) {
                return enums;
            }
        }
        return null;
    }

}
