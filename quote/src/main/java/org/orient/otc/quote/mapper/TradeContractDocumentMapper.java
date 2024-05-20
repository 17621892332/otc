package org.orient.otc.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.quote.dto.trade.TradeSettlementConfirmBookQueryDTO;
import org.orient.otc.quote.entity.TradeCloseMng;
import org.orient.otc.quote.entity.TradeContractDocument;
import org.orient.otc.quote.vo.trade.SettlementConfirmBookVO;

/**
 * 交易文件表
 * @author dzrh
 */
public interface TradeContractDocumentMapper extends BaseMapper<TradeContractDocument> {

}
