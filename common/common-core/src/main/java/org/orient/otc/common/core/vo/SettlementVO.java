package org.orient.otc.common.core.vo;

import lombok.Data;

/**
 * @author dzrh
 */
@Data
public class SettlementVO {
    /**
     * 结算结果
     */
    private Boolean isSuccess;

    private String msg;
}
