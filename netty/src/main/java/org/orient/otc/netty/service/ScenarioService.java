package org.orient.otc.netty.service;

import org.orient.otc.api.vo.BucketedVegaVO;
import org.orient.otc.api.vo.ScenarioQuoteVO;
import org.orient.otc.netty.dto.ScenarioQuoteDTO;
import org.orient.otc.netty.vo.UnderlyingVarietyVO;

import java.util.List;

/**
 * 情景分析服务
 */
public interface ScenarioService {

    /**
     * 情景分析
     * @param scenarioDTO 情景分析参数
     * @return 分析结果
     */
    List<ScenarioQuoteVO> scenario(ScenarioQuoteDTO scenarioDTO);

    /**
     * bucketedVega
     * @param scenarioDTO 计算参数
     * @return 计算结果
     */
    List<BucketedVegaVO> bucketedVega(ScenarioQuoteDTO scenarioDTO);

    /**
     * 获取合约列表
     * @return 合约信息
     */
    List<UnderlyingVarietyVO> getUnderlyingVarietyList();
}
