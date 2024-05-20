package org.orient.otc.system.dto.dictionary;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 新增字典内容
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionaryAddDto  implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 字典类型code
     */
    @ApiModelProperty(value = "字典类型code", required = true)
    @NotEmpty(message = "字典类型code不能为空")
    private String dicTypeCode;

    /**
     * 字典值
     */
    @ApiModelProperty(value = "字典值", required = true)
    @NotEmpty(message = "字典值不能为空")
    private String dicValue;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    @NotEmpty(message = "字典名称不能为空")
    private String dicName;
    /**
     * 字典排序
     */
    @ApiModelProperty(value = "字典排序", required = true)
    private Integer dicSort;
}
