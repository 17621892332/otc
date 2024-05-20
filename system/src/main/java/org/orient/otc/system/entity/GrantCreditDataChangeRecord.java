package org.orient.otc.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 授信数据变更记录
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
public class GrantCreditDataChangeRecord extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id ;
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
