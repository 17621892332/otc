package org.orient.otc.dm.dto.variety;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

/**
 * @author dzrh
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VarietyQueryDto  extends BasePage {

    /**
     * 品种ID
     */
    @Schema(name = "品种ID")
    private Integer varietyTypeId;
    /**
     * 品种代码
     */
    private String varietyCode;

    /**
     * 品种名称
     */
    private String varietyName;
}
