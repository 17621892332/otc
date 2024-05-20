package org.orient.otc.dm.dto.underlying;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.database.enums.EnabledEnum;

import javax.validation.constraints.NotNull;

/**
 * 品种信息(VarietyType)表实体类
 *
 * @author makejava
 * @since 2023-07-14 11:18:49
 */
@Data
@ApiModel
public class UnderlyingManagerEnableDto {
    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    @NotNull(message = "合约ID")
    private Integer id;

    /**
     * 是否启用
     */
    private EnabledEnum enabled;
    }

