package org.orient.otc.client.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum TradingInstType {
    EQUITY(1, "权益"),
    COMMODITY(2, "商品"),
    FIXED_INCOME(4, "固定收益"),
    FOREX(8, "外汇"),
    CREDIT(16, "信用");

    private final int code;
    private final String description;

    TradingInstType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static List<String> getInvestmentCodesAsString(int code) {
        List<Integer> codes = new ArrayList<>();
        for (TradingInstType type : TradingInstType.values()) {
            if ((code & type.getCode()) == type.getCode()) {
                codes.add(type.getCode());
            }
        }
        // 将整数列表转换为字符串列表
        return codes.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}