package org.orient.otc.client.enums;

public enum IDCardType {
    ID_CARD(1, "身份证"),
    MILITARY_OFFICER_CARD(2, "军官证"),
    HONG_KONG_MACAO_PASS(3, "港澳通行证"),
    PASSPORT(4, "护照"),
    TAIWAN_RESIDENT_CARD(5, "台胞证"),
    HONG_KONG_RESIDENT_ID(6, "香港居民身份证"),
    MACAO_RESIDENT_PASS(7, "港澳居民来往内地通行证");

    private final int code;
    private final String description;

    IDCardType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static IDCardType getByCode(int code) {
        for (IDCardType type : IDCardType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    public static IDCardType getByDescription(String description) {
        for (IDCardType type : IDCardType.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        return null;
    }
}
