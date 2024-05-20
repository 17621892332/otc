package org.orient.otc.api.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orient.otc.api.system.vo.SystemConfigVO;

import java.util.List;

/**
 * @author dzrh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemUpdateDTO {

    /**
     * 系统配置信息
     */
    private List<SystemConfigVO> configList;

}
