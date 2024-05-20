package org.orient.otc.dm.dto.variety;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 品种信息(VarietyType)表实体类
 *
 * @author makejava
 * @since 2023-07-14 11:18:49
 */
@Data
@ApiModel
public class VarietyTypeDeleteDto {
    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    @NotNull(message = "产业链ID不能为空")
    private Integer id;
    }

