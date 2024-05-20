package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 情景分析请求参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class ScenarioTemplate extends BaseEntity implements Serializable {
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 价格上限
     */
    private BigDecimal upPrice;

    /**
     * 价格下限
     */
    private BigDecimal downPrice;

    /**
     * 价格间隔
     */
    private BigDecimal intervalPrice;


    /**
     * 波动率上限
     */
    private BigDecimal upVol;
    /**
     * 波动率下限
     */
    private BigDecimal downVol;
    /**
     * 波动率间隔
     */
    private BigDecimal intervalVol;
    /**
     * 观察天数(工作日)
     */
    @Max(value = 180,message = "观察天数不能大于180天")
    @Min(value = 1,message = "观察天数必须大于0")
    private Integer dayCount;

    /**
     * 日期间隔
     */
    private Integer intervalDate;
    /**
     * 报告类型
     */
    private String reportType;

    /**
     * 是否客户方向
     */
    private Boolean isClient;

    /**
     * 是否固定波动率
     */
    private Boolean isFixedVol;
}
