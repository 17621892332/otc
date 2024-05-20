package org.orient.otc.openapi.service;

import org.orient.otc.api.finoview.dto.VolatilityDTO;

import java.util.List;

public interface FinoviewService {

    /**
     * 发送所有波动率数据到繁微
     * @return  发送数量
     */
    Integer sendAllVolToFinoview();

    /**
     * 所有单个波动率合约到繁微
     * @param volatilityDTOList 波动率数据
     */
    void sendVolToFinoview(List<VolatilityDTO> volatilityDTOList);
}
