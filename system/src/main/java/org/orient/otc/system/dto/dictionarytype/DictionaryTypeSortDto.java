package org.orient.otc.system.dto.dictionarytype;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 修改字典类型序号dto
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionaryTypeSortDto implements Serializable {

    @ApiModelProperty( value = "字典类型排序列表", required = true)
    List<DictionaryTypeSortItemDto> sortItemDtoList;
}
