package org.orient.otc.dm.dto.variety;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 品种信息(VarietyType)表实体类
 *
 * @author makejava
 * @since 2023-07-14 11:18:49
 */
@Data
@ApiModel
public class VarietyTypeAddDto {

    /**
     * 产业链名称
     */
    @ApiModelProperty(value = "产业链名称")
    @NotEmpty(message = "产业链名称不能为空")
    private String typeName;

    @ApiModelProperty(value = "排序")
    private Integer typeSort;

    }

