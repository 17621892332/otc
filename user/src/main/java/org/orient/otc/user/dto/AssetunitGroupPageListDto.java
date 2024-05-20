package org.orient.otc.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.core.dto.BasePage;

@Data
@ApiModel("薄记账户组分页查询dto")
public class AssetunitGroupPageListDto extends BasePage {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("创建人")
    private Integer creatorId;
}
