package org.orient.otc.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.orient.otc.message.entity.MailTemplate;

@Mapper
public interface MailTemplateMapper extends BaseMapper<MailTemplate> {
}
