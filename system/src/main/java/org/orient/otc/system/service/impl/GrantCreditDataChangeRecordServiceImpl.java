package org.orient.otc.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.system.dto.grantcreditdatachangerecord.GrantCreditDataChangeRecordAddDto;
import org.orient.otc.system.entity.GrantCreditDataChangeRecord;
import org.orient.otc.system.mapper.GrantCreditDataChangeRecordMapper;
import org.orient.otc.system.service.GrantCreditDataChangeRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GrantCreditDataChangeRecordServiceImpl extends ServiceImpl<GrantCreditDataChangeRecordMapper, GrantCreditDataChangeRecord> implements GrantCreditDataChangeRecordService {
    @Override
    public void add(GrantCreditDataChangeRecordAddDto addDto) {
        GrantCreditDataChangeRecord entiy = new GrantCreditDataChangeRecord();
        BeanUtils.copyProperties(addDto,entiy);
        this.save(entiy);
    }
}
