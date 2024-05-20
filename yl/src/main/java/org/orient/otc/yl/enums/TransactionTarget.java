package org.orient.otc.yl.enums;

import java.util.List;

public enum TransactionTarget {
    INVEST(1, "投资"),
    ARBITRAGE(2, "套利"),
    HEDGING(3, "套保");

    private final int key;
    private final String value;

    TransactionTarget(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static String convertRolesToKeys(List<String> rolesAsString) {
        StringBuilder resultBuilder = new StringBuilder();

        for (String roleStr : rolesAsString) {
            TransactionTarget role = getByValue(roleStr);
            if (role != null) {
                if (resultBuilder.length() > 0) {
                    resultBuilder.append(",");
                }
                resultBuilder.append(role.getKey());
            }
        }

        return resultBuilder.toString();
    }

    public static TransactionTarget getByValue(String value) {
        for (TransactionTarget role : values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }
        return null; // 如果没有找到匹配的角色
    }
}
