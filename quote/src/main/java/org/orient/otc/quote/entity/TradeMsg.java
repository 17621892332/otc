package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.quote.enums.OpenOrCloseEnum;

import java.io.Serializable;

/**
 * 交易简讯
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class TradeMsg extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * 交易ID或者平仓ID
     */
    @ApiModelProperty(value = "交易ID或者平仓ID")
    private Integer tradeId;

    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型")
    private OpenOrCloseEnum tradeType;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String msgInfo;

}
