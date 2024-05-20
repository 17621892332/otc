package org.orient.otc.system.service;

import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.system.dto.grantcreditdatachangerecord.GrantCreditDataChangeRecordAddDto;
import org.orient.otc.system.entity.GrantCreditDataChangeRecord;

public interface GrantCreditDataChangeRecordService extends IServicePlus<GrantCreditDataChangeRecord> {
    void add(GrantCreditDataChangeRecordAddDto addDto);
}
