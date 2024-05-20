package org.orient.otc.quote.service;

import org.orient.otc.api.quote.dto.risk.RiskMarkDto;
import org.orient.otc.quote.entity.RiskMark;

import java.math.BigDecimal;

/**
 * 自定义行情服务
 */
public interface RiskMarkService {
    /**
     * 添加自定义行情
     * @param riskMark 自定义行情信息
     * @return 添加结果
     */
    String insertRiskMark(RiskMark riskMark);
    /**
     * 删除自定义行情
     * @param riskMarkDto 自定义行情信息
     * @return 删除结果
     */
    String deleteRiskMark(RiskMarkDto riskMarkDto);

    /**
     * 获取合约设定的合约价格
     * @param riskMarkDto 合约代码
     * @return 合约价格
     */
    BigDecimal getRiskMark(RiskMarkDto riskMarkDto);
}
