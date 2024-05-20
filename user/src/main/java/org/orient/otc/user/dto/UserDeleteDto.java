package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.user.enums.SexEnums;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("删除用户信息")
public class UserDeleteDto {
    /**
     * 用户ID
     */
    @NotNull(message="id不能为空")
    @ApiModelProperty(value = "用户ID",required = true)
    private Integer id;

}
