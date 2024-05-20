package org.orient.otc.system.dto.dictionarytype;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 修改字典类型dto
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionaryTypeUpdateDto implements Serializable {

    /**
     * 字典类型ID
     */
    @ApiModelProperty( value = "字典类型ID", required = true)
    @NotEmpty(message = "字典类型ID不能为空")
    private String dicTypeId;

    /**
     * 字典类型code
     */
    @ApiModelProperty( value = "字典类型code", required = true)
    @NotEmpty(message = "字典类型code不能为空")
    private String dicTypeCode;

    /**
     * 名称
     */
    @ApiModelProperty( value = "名称", required = true)
    @NotEmpty(message = "字典类型名称不能为空")
    private String dicTypeName;

    /**
     * 字典排序
     */
    @ApiModelProperty( value = "字典排序", required = true)
    private Integer dicTypeSort;
    /**
     * 描述
     */
    @ApiModelProperty( value = "描述", required = true)
    private String deScript;
}
