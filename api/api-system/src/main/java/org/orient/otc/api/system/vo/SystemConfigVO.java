package org.orient.otc.api.system.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统配置信息
 * @author dzrh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigVO {

    private Integer id;


    /**
     * 无风险利率
     */
    private String configKey;

    /**
     * 股息率
     */
    private String configValue;
}
