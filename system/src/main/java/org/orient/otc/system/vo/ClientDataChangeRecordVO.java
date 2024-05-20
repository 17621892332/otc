package org.orient.otc.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.database.vo.BaseVO;

import java.io.Serializable;
import java.util.List;

/**
 * 客户数据变更记录VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ClientDataChangeRecordVO extends BaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id ;
    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    private String clientCode ;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Integer clientId ;
    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String clientName ;
    /**
     * 操作人名称
     */
    @ApiModelProperty(value = "操作人名称")
    private String creatorName ;
    /**
     * 变更类型
     */
    @ApiModelProperty(value = "变更类型")
    private DataChangeTypeEnum changeType ;
    /**
     * 变更字段
     */
    @ApiModelProperty(value = "变更字段")
    private String changeFields ;
    /**
     * 变更字段lisi
     */
    @ApiModelProperty(value = "变更字段list")
    private List<DiffObjectVO> changeFieldObjectList ;

    /**
     * 所有变更的key
     */
    @ApiModelProperty(value = "变更的key")
    private String changeKey;

}
