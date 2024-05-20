package org.orient.otc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordDetailDto;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordDTO;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordPageDto;
import org.orient.otc.system.entity.TradeDataChangeRecord;
import org.orient.otc.system.vo.TradeDataChangeRecordVO;

public interface TradeDataChangeRecordService extends IServicePlus<TradeDataChangeRecord> {
    String add(TradeDataChangeRecordDTO dto);

    IPage<TradeDataChangeRecordVO> selectByPage(TradeDataChangeRecordPageDto dto);

    TradeDataChangeRecordVO getDetails(TradeDataChangeRecordDetailDto dto);
}
