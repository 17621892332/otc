package org.orient.otc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.system.dto.clientdatachangerecord.ClientDataChangeRecordAddDto;
import org.orient.otc.system.dto.clientdatachangerecord.ClientDataChangeRecordDetailDto;
import org.orient.otc.system.dto.clientdatachangerecord.ClientDataChangeRecordPageDto;
import org.orient.otc.system.entity.ClientDataChangeRecord;
import org.orient.otc.system.vo.ClientDataChangeRecordVO;

public interface ClientDataChangeRecordService extends IServicePlus<ClientDataChangeRecord> {
    IPage<ClientDataChangeRecordVO> selectByPage(ClientDataChangeRecordPageDto dto);
    ClientDataChangeRecordVO getDetails(ClientDataChangeRecordDetailDto dto);
    String add(ClientDataChangeRecordAddDto dto);
}
