package org.orient.otc.quote.vo;

import lombok.Data;
import org.orient.otc.api.dm.vo.InstrumentInfoVo;
import org.orient.otc.quote.dto.risk.ExchangeRealTimePos;

import java.util.List;
import java.util.Map;

@Data
public class InstrumentInfoMap {
    /**
     * key=合约代码,vaule=合约信息
     */
    Map<String, InstrumentInfoVo> instrumentInfoVoMap;
    /**
     * key=持仓redis的key,value是持仓redis的key取出的value数组
     */
    Map<String, List<ExchangeRealTimePos>> exchangeRealTimePosMap;
}
