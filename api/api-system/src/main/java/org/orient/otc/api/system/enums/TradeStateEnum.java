package org.orient.otc.api.system.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author dzrh
 */

public enum TradeStateEnum {
    /**
     * 新增待确认
     */
    added(1, "新增待确认"),

    modified(2, "修改待确认"),
    approval(10, "审批中"),
    confirmed(11, "确认成交"),
    rejectEarlyClose(12, "提前终止拒绝"),
    checkToClose(21, "平仓待复核"),
    checkToExercise(22, "行权待复核"),
    partClosed(11, "部分平仓"),
    closed(31, "已平仓"),
    expired(32, "已到期"),
    exercised(33, "已执行"),
    knockoutTerminate(34, "敲出终止"),

    ;
    private final Integer key;
    private final String desc;

    TradeStateEnum(Integer key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public Integer getKey() {
        return this.key;
    }

    public String getDesc() {
        return this.desc;
    }

    public static TradeStateEnum getTradeTypeByKey(Integer key) {
        if (key == null) {
            return null;
        }
        for (TradeStateEnum enums : TradeStateEnum.values()) {
            if (enums.getKey().equals(key)) {
                return enums;
            }
        }
        return null;
    }

    /**
     * 获取平仓状态
     * @return  交易状态
     */
    public static List<TradeStateEnum> getCloseStateList() {
        return Arrays.asList(closed, expired, knockoutTerminate);
    }
    /**
     * 获取存续的状态
     * @return  交易状态
     */
    public static List<TradeStateEnum> getLiveStateList() {
        return Arrays.asList(confirmed,partClosed);
    }
    /**
     * 获取存在平仓的状态
     * @return  交易状态
     */
    public static List<TradeStateEnum> getHaveCloseStateList() {
        return Arrays.asList(partClosed,closed, expired, knockoutTerminate);
    }
    public static TradeStateEnum getTradeTypeByDesc(String desc) {
        if (StringUtils.isEmpty(desc)) {
            return null;
        }
        for (TradeStateEnum enums : TradeStateEnum.values()) {
            if (enums.getDesc().equals(desc)) {
                return enums;
            }
        }
        return null;
    }
}
