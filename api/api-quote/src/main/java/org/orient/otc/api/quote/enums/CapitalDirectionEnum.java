package org.orient.otc.api.quote.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/** 资金方向
 * @author dzrh
 */

@Getter
public enum CapitalDirectionEnum {
    /**
     * 出金
     */
    out("out", "出金"),
    /**
     * 入金
     */
    in("in", "入金"),
    /**
     * 权利金支出
     */
    premiumOut("premiumOut", "权利金支出"),
    /**
     * 权利金收入
     */
    premiumIn("premiumIn", "权利金收入"),
    /**
     * 平仓/行权支出
     */
    exerciseOut("exerciseOut", "平仓/行权支出"),
    /**
     * 平仓/行权收入
     */
    exerciseIn("exerciseIn", "平仓/行权收入"),
    /**
     * 票息支出
     */
    couponOut("couponOut", "票息支出"),
    /**
     * 票息收入
     */
    couponIn("couponIn", "票息收入"),
    /**
     * 其他支出
     */
    otherOut("otherOut", "其他支出"),
    /**
     * 其他收入
     */
    otherIn("otherIn", "其他收入"),
    /**
     * 互换支出
     */
    exchangeOut("exchangeOut", "互换支出"),
    /**
     * 互换收入
     */
    exchangeIn("exchangeIn", "互换收入")
    ;
    private final String key;
    private final String desc;

    /**
     * 获取所有出金列表
     * @return  出入金类别
     */
    public static List<CapitalDirectionEnum> getOut() {
        return Arrays.asList(
                out,
                premiumOut,
                exerciseOut,
                couponOut,
                otherOut,
                exchangeOut
        );
    }

    @Override
    public String toString() {
        return this.desc;
    }

    CapitalDirectionEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public static CapitalDirectionEnum getCapitalStatusByKey(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (CapitalDirectionEnum enums : CapitalDirectionEnum.values()){
            if (enums.getKey().equals(key)){
                return enums;
            }
        }
        return null;
    }
    public static CapitalDirectionEnum getCapitalStatusByDesc(String desc){
        if (StringUtils.isEmpty(desc)){
            return null;
        }
        for (CapitalDirectionEnum enums : CapitalDirectionEnum.values()){
            if (enums.getDesc().equals(desc)){
                return enums;
            }
        }
        return null;
    }
}
