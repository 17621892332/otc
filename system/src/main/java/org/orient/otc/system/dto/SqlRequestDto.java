package org.orient.otc.system.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class SqlRequestDto {
    @ApiModelProperty( value = "sqlconfig的code", required = true)
    @NotNull
    private String code;
    @ApiModelProperty( value = "sql参数", required = true)
    @NotNull
    private Map<String,Object> param;
}
