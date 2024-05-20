package org.orient.otc.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.orient.otc.message.entity.MailSendRecord;

@Mapper
public interface MailSendRecordMapper extends BaseMapper<MailSendRecord> {
}
