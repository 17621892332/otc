package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.quote.dto.SnowKnockedinLogPageDto;
import org.orient.otc.quote.entity.SnowKnockedinLog;

public interface SnowKnockedinLogService {

    IPage<SnowKnockedinLog> selectListByPage(SnowKnockedinLogPageDto dto);
}
