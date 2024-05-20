package org.orient.otc.api.client.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/** 授信状态枚举
 * @author dzrh
 */

@Getter
public enum GrantCreditApprovalStatusEnum {

    unapproved("unapproved", "未审批"),
    approvaling("approvaling", "审批中"),
    approved("approved", "已审批"),
    refuse("refuse", "已拒绝")
    ;
    private final String key;
    private final String desc;

    @Override
    public String toString() {
        return this.desc;
    }

    GrantCreditApprovalStatusEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public static GrantCreditApprovalStatusEnum getCapitalStatusByKey(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        for (GrantCreditApprovalStatusEnum enums : GrantCreditApprovalStatusEnum.values()){
            if (enums.getKey().equals(key)){
                return enums;
            }
        }
        return null;
    }
    public static GrantCreditApprovalStatusEnum getCapitalStatusByDesc(String desc){
        if (StringUtils.isEmpty(desc)){
            return null;
        }
        for (GrantCreditApprovalStatusEnum enums : GrantCreditApprovalStatusEnum.values()){
            if (enums.getDesc().equals(desc)){
                return enums;
            }
        }
        return null;
    }
}
