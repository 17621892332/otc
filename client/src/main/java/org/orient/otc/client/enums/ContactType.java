package org.orient.otc.client.enums;

import java.util.List;

public enum ContactType {
    CONTACT_PERSON(1, "联系人"),
    LEGAL_REPRESENTATIVE(2, "法定代表人"),
    TRANSACTION_ISSUER(3, "交易下达人"),
    AUTHORIZED_SIGNATORY(4, "授权签署人");

    private final int key;
    private final String value;

    ContactType(int key, String value) {
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
            ContactType role = getByValue(roleStr);
            if (role != null) {
                if (resultBuilder.length() > 0) {
                    resultBuilder.append(",");
                }
                resultBuilder.append(role.getKey());
            }
        }

        return resultBuilder.toString();
    }

    public static ContactType getByValue(String value) {
        for (ContactType role : values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }
        return null; // 如果没有找到匹配的角色
    }
}
