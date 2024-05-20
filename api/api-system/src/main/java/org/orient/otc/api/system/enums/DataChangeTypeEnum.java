package org.orient.otc.api.system.enums;

import lombok.Getter;

/**
 * 数据变更类型枚举
 * @author chengqiang
 */
@Getter
public enum DataChangeTypeEnum {
    add("add","新增"),
    delete("delete","删除"),
    update("update","更新"),
    clientBankInfoAdd("clientBankInfoAdd","客户银行卡新增"),
    clientBankInfoUpdate("clientBankInfoUpdate","客户银行卡更新"),
    clientBankInfoDelete("clientBankInfoDelete","客户银行卡删除"),
    clientDutyAdd("clientDutyAdd","客户人员新增"),
    clientDutyYLAdd("clientDutyYLAdd","客户人员新增-镒链同步"),
    clientDutyUpdate("clientDutyUpdate","客户人员更新"),
    clientDutyDelete("clientDutyDelete","客户人员删除"),
    ;
    private final String key;
    private final String desc;
    DataChangeTypeEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
    public String getKey() {
        return this.key;
    }

    public String getDesc() {
        return this.desc;
    }
}
