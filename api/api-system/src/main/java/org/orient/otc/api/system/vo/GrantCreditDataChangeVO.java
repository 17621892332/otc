package org.orient.otc.api.system.vo;

import lombok.Data;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;

@Data
public class GrantCreditDataChangeVO {
    /**
     * 变更前对象
     */
    GrantCreditDataChangeDetailVO orgVO;
    /**
     * 变更后对象
     */
    GrantCreditDataChangeDetailVO destVO;

    /**
     * 变更类型
     */
    private DataChangeTypeEnum changeType;
}
