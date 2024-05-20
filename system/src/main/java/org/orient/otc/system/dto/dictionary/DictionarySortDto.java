package org.orient.otc.system.dto.dictionary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 修改字典排序
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictionarySortDto implements Serializable {

    @ApiModelProperty( value = "字典排序列表")
    private List<DictionarySortItemDto> sortItemDtoList;
}
