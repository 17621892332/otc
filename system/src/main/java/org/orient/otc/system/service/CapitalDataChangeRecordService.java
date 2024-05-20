package org.orient.otc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.system.dto.capitaldatachangerecord.CapitalDataChangeRecordAddDto;
import org.orient.otc.system.dto.capitaldatachangerecord.CapitalDataChangeRecordPageDto;
import org.orient.otc.system.entity.CapitalDataChangeRecord;
import org.orient.otc.system.vo.CapitalDataChangeRecordVO;

public interface CapitalDataChangeRecordService extends IServicePlus<CapitalDataChangeRecord> {
    IPage<CapitalDataChangeRecordVO> selectByPage(CapitalDataChangeRecordPageDto dto);

    void add(CapitalDataChangeRecordAddDto addDto);
}
