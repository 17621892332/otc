package org.orient.otc.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.quote.dto.trade.TradeSettlementConfirmBookQueryDTO;
import org.orient.otc.quote.entity.TradeCloseMng;
import org.orient.otc.quote.entity.TradeContractRel;
import org.orient.otc.quote.vo.trade.SettlementConfirmBookVO;

/**
 * 交易与文件关系表
 * @author dzrh
 */
public interface TradeContractRelMapper extends BaseMapper<TradeContractRel> {

}
