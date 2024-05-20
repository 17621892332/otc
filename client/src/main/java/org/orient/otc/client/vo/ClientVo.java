package org.orient.otc.client.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.client.entity.BankCardInfo;
import org.orient.otc.client.entity.ClientLevel;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClientVo {
    private Integer id;

    @ApiModelProperty(value = "客户编号")
    private String code;

    @ApiModelProperty(value = "客户名称")
    private String name;

    @ApiModelProperty(value = "客户简称")
    private String shortName;

    @ApiModelProperty(value = "客户等级id")
    private Integer levelId;

    @ApiModelProperty(value = "客户等级")
    private ClientLevel clientLevel;

    private int isDeleted;
    private Integer creatorId;
    private Integer updatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "客户的银行账户信息")
    List<BankCardInfo> bankCardInfoList;
}
