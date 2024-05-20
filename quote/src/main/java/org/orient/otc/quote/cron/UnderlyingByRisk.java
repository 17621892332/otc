package org.orient.otc.quote.cron;

import lombok.Data;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.api.quote.vo.VolatilityVO;
import org.orient.otc.quote.dto.risk.ExchangeRealTimePos;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dzrh
 */
@Data
public class UnderlyingByRisk {


    /**
     * 标的资产码
     */
    private String underlyingCode;

    /**
     * 标的资产码
     */
    private String exchangeUnderlyingCode;

    /**
     * 合约乘数
     */
    private Integer contractSize;

    /**
     * 标的名称
     */
    private String underlyingName;

    /**
     * 品种id
     */
    private Integer varietyId;

    private String varietyCode;

    /**
     * 最新价格
     */
    private BigDecimal lastPrice;
    /**
     * 股息率
     */
    private BigDecimal underlyingDividendYield;

    /**
     * 计算时间
     */
    private  long evaluationTime;

    /**
     * 波动率
     */
    private VolatilityVO midVolatility;


    /**
     * 场外交易记录
     */
    private List<TradeMngVO> tradeMngVOList;



    /**
     * 场内交易记录
     */
    private List<ExchangeRealTimePos> exchangeRealTimePosList;
}
