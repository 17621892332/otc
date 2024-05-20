package org.orient.otc.netty.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.channel.Channel;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.api.netty.dto.DeltaAdjustmentDto;
import org.orient.otc.netty.dto.RiskInfoQueryByPageDto;
import org.orient.otc.netty.dto.RiskInfoQueryDto;
import org.orient.otc.api.quote.dto.risk.TradeRiskCacularResult;
import org.orient.otc.netty.dto.RiskTimeEditDTO;
import org.orient.otc.netty.vo.RiskTotalVo;

import java.util.List;
import java.util.Set;

public interface RiskService {

    RiskTotalVo getRiskInfoList(RiskInfoQueryDto dto);

    /**
     * 修改搜索条件
     * @param value 搜索条件
     */
    void  modifySearchCriteria(Channel channel, RiskInfoQueryDto value);

    /**
     * 获取风险计算结果
     * @param dto 请求参数
     * @return 风险计算结果
     */
    List<TradeRiskCacularResult> getTradeRiskCacularResult(RiskInfoQueryDto dto);

    void sendTradeRiskCacularResult(Channel channel,RiskInfoQueryDto dto);

    Set<String> getVarietyList();
    List<ClientVO> getClientList();

    void  editDeltaAdjustment(DeltaAdjustmentDto dto);

    /**
     * 修改风险计算时间
     * @param riskTimeEditDto 目标时间对象
     */
    void editRiskTime(RiskTimeEditDTO riskTimeEditDto);


    Page<TradeRiskCacularResult> getTradeListByPage(RiskInfoQueryByPageDto dto);
}
