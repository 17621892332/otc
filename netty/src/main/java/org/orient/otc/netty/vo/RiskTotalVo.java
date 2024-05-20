package org.orient.otc.netty.vo;

import lombok.Data;
import org.orient.otc.api.netty.vo.RiskInfoVo;
import org.orient.otc.api.quote.dto.risk.TradeRiskCacularResult;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dzrh
 */
@Data
public class RiskTotalVo {
    /**
     * 风险汇总信息
     */
  private   List<RiskInfoVo> riskInfoVoList;
    /**
     * 交易记录详情
     */
  private   List<TradeRiskCacularResult>  detailList;

    /**
     * 风险计算时间
     */
  private LocalDateTime riskTime;
}
