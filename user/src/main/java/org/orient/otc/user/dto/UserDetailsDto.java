package org.orient.otc.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("获取用户详情dto")
public class UserDetailsDto {
    /**
     * 用户ID
     */
    @NotNull(message="id不能为空")
    @ApiModelProperty(value = "用户ID",required = true)
    private Integer id;

}
