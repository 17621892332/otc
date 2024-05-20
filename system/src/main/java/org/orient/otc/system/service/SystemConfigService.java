package org.orient.otc.system.service;

import org.orient.otc.api.system.dto.SystemUpdateDTO;
import org.orient.otc.api.system.vo.SystemConfigVO;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.system.dto.IntegerIdDTO;
import org.orient.otc.system.entity.SystemConfig;

import java.util.List;

/**
 * @author dzrh
 */
public interface SystemConfigService extends IServicePlus<SystemConfig> {

    /**
     * 获取系统配置信息
     * @return 系统配置信息
     */
    List<SystemConfigVO> getSystemConfigList();

    /**
     * 更新系统配置
     * @param systemUpdateDTO  系统配置
     * @return 是否更新成功
     */
    Boolean updateSystemConfig(SystemUpdateDTO systemUpdateDTO);

    Boolean delSystemConfig(IntegerIdDTO integerIdDTO);
}
