package org.orient.otc.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 情景分析计算
 */
@Data
public class ScenarioDTO  implements Serializable {

    /**
     * 配置模板数据
     */
    private GeneralDTO general;

    /**
     * 交易数据
     */
    private List<TradeDataDTO> tradeData;
}
