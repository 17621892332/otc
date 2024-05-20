package org.orient.otc.dm.dto.underlying;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;
import org.orient.otc.common.database.enums.EnabledEnum;
import org.orient.otc.api.dm.enums.UnderlyingState;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema
public class UnderlyingManagerPageQueryDto extends BasePage {

    /**
     * 交易所
     */
    @Schema(name = "交易所")
    private String exchange;

    /**
     * 标的资产码
     */
    @Schema(name = "标的资产码")
    private String underlyingCode;

    @Schema(name = "标的类型")
    private List<Integer> varietyIdList;

    /**
     * 到期日期,格式: yyyy-MM-dd
     */
    @Schema(name = "到期日期-开始时间")
    private LocalDate expireDateStart;

    /**
     * 到期日期,格式: yyyy-MM-dd
     */
    @Schema(name = "到期日期-结束时间")
    private LocalDate expireDateEnd;

    @Schema(name = "是否启用")
    private EnabledEnum isEnabled;

    @Schema(name = "标的状态")
    private UnderlyingState underlyingState;

    /**
     * 风险预警 0:否 1:是
     */
    @Schema(name = "是否风险预警")
    private Integer isRiskWarning;
}
