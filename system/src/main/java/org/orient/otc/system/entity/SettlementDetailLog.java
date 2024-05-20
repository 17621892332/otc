package org.orient.otc.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.system.enums.SuccessStatusEnum;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
public class SettlementDetailLog extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty( value = "任务名称", required = true)
    private String taskName;
    @ApiModelProperty( value = "收盘日期", required = true)
    private LocalDate settlementDate;
    @ApiModelProperty( value = "是否成功", required = true)
    private SuccessStatusEnum successStatus;
    @ApiModelProperty( value = "错误信息", required = true)
    private String message;
}
