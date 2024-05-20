package org.orient.otc.api.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;

import java.time.LocalDate;

/**
 * 授信历史记录新增DTO
 * @author chengqiang
 */
@Data
public class APIGrantCreditDataChangeRecordAddDto {
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId ;
    /**
     * 资金记录ID
     */
    @ApiModelProperty(value = "授信记录ID")
    private Integer grantCreditId ;
    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    private LocalDate startDate ;
    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

    /**
     * 授信方向
     */
    @ApiModelProperty(value = "授信方向")
    private int direction;
    /**
     * 变更类型
     */
    @ApiModelProperty(value = "变更类型")
    private DataChangeTypeEnum changeType ;
    /**
     * 变更字段json字符串(应该是一个json数组)
     */
    @ApiModelProperty(value = "变更字段")
    private String changeFields ;
}
