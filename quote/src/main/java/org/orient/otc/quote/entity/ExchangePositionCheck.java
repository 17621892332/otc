package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.api.quote.enums.SuccessStatusEnum;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *  场内持仓校验结果
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
@ApiModel
public class ExchangePositionCheck extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 场内账号
     */
    private String investorId;

    private String instrumentId;

    private LocalDate tradingDay;

    /**
     * '2':多头，'3':空头
     */
    private Integer posiDirection;

    /**
     * 校验详情
     */
    private String checkMsg;

    private SuccessStatusEnum status;
}
