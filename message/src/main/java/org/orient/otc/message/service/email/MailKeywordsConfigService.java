package org.orient.otc.message.service.email;

import org.orient.otc.message.dto.MailKeywordsConfigAddDto;
import org.orient.otc.message.dto.MailKeywordsConfigDeleteDto;
import org.orient.otc.message.dto.MailKeywordsConfigUpdateDto;
import org.orient.otc.message.entity.MailKeywordsConfig;

import java.util.List;

public interface MailKeywordsConfigService {
    String add(MailKeywordsConfigAddDto dto);

    List<MailKeywordsConfig> getAll();

    String updateConfig(MailKeywordsConfigUpdateDto dto);

    String deleteConfig(MailKeywordsConfigDeleteDto dto);
}
