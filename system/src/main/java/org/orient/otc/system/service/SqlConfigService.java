package org.orient.otc.system.service;

import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.system.dto.SqlRequestDto;
import org.orient.otc.system.entity.SqlConfig;

import java.util.List;
import java.util.Map;

public interface SqlConfigService extends IServicePlus<SqlConfig> {
    Object getSqlResult(SqlRequestDto sqlRequest);
}
