package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.quote.dto.trade.TradeDetailPageListDto;
import org.orient.otc.quote.entity.ObsTradeDetail;
import org.orient.otc.quote.vo.trade.ObsTradeDetailVo;

/**
 * @author dzrh
 */
public interface TradeDetailService extends IServicePlus<ObsTradeDetail> {

    IPage<ObsTradeDetailVo> selectListByPage(TradeDetailPageListDto dto);
}
