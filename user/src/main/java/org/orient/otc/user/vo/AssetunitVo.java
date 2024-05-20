package org.orient.otc.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.user.entity.AssetunitGroup;
import org.orient.otc.user.entity.AssetunitUser;
import org.orient.otc.user.entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "簿记账户")
public class AssetunitVo {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("簿记账户名称")
    private String name;

    @ApiModelProperty("簿记账户组ID")
    private Integer groupId;

    @ApiModelProperty("簿记账户组")
    private AssetunitGroup assetunitGroup;

    @ApiModelProperty("基础币种")
    private String baseCurrency;

    @ApiModelProperty("交易员列表")
    List<AssetUnitUserVo> assetunitUserList;

    @ApiModelProperty("操作员")
    private User user;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern  = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
