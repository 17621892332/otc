package org.orient.otc.client.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.client.entity.Client;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
@ApiModel
public class BankCardInfoDeleteDto implements Serializable {
    @ApiModelProperty(value = "ID")
    @NotNull(message = "id不能为空")
    private Integer id;
    @ApiModelProperty(value = "备注")
    private String remark;

}
