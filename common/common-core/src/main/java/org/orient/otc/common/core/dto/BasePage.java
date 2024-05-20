package org.orient.otc.common.core.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页参数
 */
@Data
public class BasePage {

    @ApiModelProperty(value = "每页条数",required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "第几页",required = true)
    private Integer pageNo;
}
