package org.orient.otc.quote.dto.settlementbook;

import lombok.Data;
import org.orient.otc.quote.enums.SettlementBookTypeEnum;

import java.util.List;

/**
 * @author dzrh
 */
@Data
public class SettlementBook {
    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 日期
     */
    private String  closeDateSrt;

    private SettlementBookTypeEnum settlementBookType;
    /**
     * 结算确认书数据
     */
    private List<SettlementBookData> data;
}
