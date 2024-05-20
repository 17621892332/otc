package org.orient.otc.api.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.common.core.vo.DiffObjectVO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 客户变更记录新增dto
 */
@Data
public class ClientChangeRecordAddDto {
    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    @NotEmpty(message = "客户编号不能为空")
    private String clientCode ;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    @NotNull(message = "客户不能为空")
    private Integer clientId ;

    /**
     * 变更类型
     */
    @ApiModelProperty(value = "变更类型")
    @NotNull(message = "变更类型不能为空")
    private DataChangeTypeEnum changeType ;
    /**
     * 变更字段
     */
    @ApiModelProperty(value = "变更字段")
    @NotEmpty(message = "变更字段不能为空")
    private String changeFields ;
    /**
     * 变更字段obj
     */
    @ApiModelProperty(value = "变更字段obj")
    @NotEmpty(message = "变更字段obj不能为空")
    private List<DiffObjectVO> changeFieldObjectList ;
}
