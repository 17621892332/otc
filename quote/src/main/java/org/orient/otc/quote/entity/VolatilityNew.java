package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.InterpolationMethodEnum;
import org.orient.otc.api.quote.enums.VolTypeEnum;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.quote.handler.VolatityDataListTypeHandler;
import org.orient.otc.quote.handler.VolatityDeltaDataListTypeHandler;
import org.orient.otc.quote.dto.volatility.VolatityDataDto;
import org.orient.otc.quote.dto.volatility.VolatityDeltaDataDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 最新波动率
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(autoResultMap = true)
@ApiModel
public class VolatilityNew extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 标的ID
     */
    @ApiModelProperty(value = "标的code")
    @NotNull
    private String underlyingCode;

    /**
     * 波动率类型
     */
    @ApiModelProperty(value = "波动率类型")
    @NotNull
    private VolTypeEnum volType;

    /**
     * 当前波动率表格数据
     */
    @ApiModelProperty(value = "当前波动率表格数据")
    @NotNull
    @TableField(typeHandler = VolatityDataListTypeHandler.class)
    private List<VolatityDataDto> data;
    /**
     * 当前波动率表格数据
     */
    @ApiModelProperty(value = "当前delta波动率表格数据")
    @NotNull
    @TableField(typeHandler = VolatityDeltaDataListTypeHandler.class)
    private List<VolatityDeltaDataDto> deltaData;


    /**
     * 插值方法
     */
    @ApiModelProperty(value = "插值方法")
    @NotNull
    private InterpolationMethodEnum interpolationMethod;
}
