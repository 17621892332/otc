package org.orient.otc.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.orient.otc.quote.dto.trade.TradeQueryDTO;
import org.orient.otc.quote.dto.trade.TradeSettlementConfirmBookQueryDTO;
import org.orient.otc.quote.entity.TradeCloseMng;
import org.orient.otc.quote.vo.trade.HistoryTradeMngVO;
import org.orient.otc.quote.vo.trade.SettlementConfirmBookVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 交易平仓
 * @author dzrh
 */
public interface TradeCloseMngMapper extends BaseMapper<TradeCloseMng> {

    Page<SettlementConfirmBookVO> selectSettlementConfirmBook(Page<SettlementConfirmBookVO> page,TradeSettlementConfirmBookQueryDTO dto);
}
