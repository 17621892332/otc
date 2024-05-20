package org.orient.otc.dm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.api.dm.enums.MainContractEnum;
import org.orient.otc.api.dm.enums.UnderlyingState;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class UnderlyingManager extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 标的资产码
     */
    @ApiModelProperty(value = "标的资产码")
    private String underlyingCode;

    /**
     * 标的资产码
     */
    @ApiModelProperty(value = "场内标的资产码")
    private String exchangeUnderlyingCode;

    /**
     * 标的名称
     */
    @ApiModelProperty(value = "标的名称")
    private String underlyingName;

    /**
     * 品种id
     */
    @ApiModelProperty(value = "品种id")
    private Integer varietyId;

    /**
     * 涨跌停板幅度
     */
    @ApiModelProperty(value = "涨跌停板幅度")
    private BigDecimal upDownLimit;

    /**
     * 保证金比例
     */
    @ApiModelProperty(value = "保证金比例")
    private BigDecimal marginRate;

    /**
     * 最小价格变动单位
     */
    @ApiModelProperty(value = "最小价格变动单位")
    private BigDecimal priceTick;

    /**
     * 报价单位
     */
    @ApiModelProperty(value = "报价单位")
    private String quoteUnit;

    /**
     * 合约乘数
     */
    @ApiModelProperty(value = "合约乘数")
    private Integer contractSize;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private UnderlyingState underlyingState;

    /**
     * 是否启用
     */
    private Integer enabled;

    /**
     * 交易所代码
     */
    private String exchange;

    /**
     * 上市日期,格式: yyyy-MM-dd
     */
    private LocalDate createDate;
    /**
     * 到期日期,格式: yyyy-MM-dd
     */
    private LocalDate expireDate;

    /**
     * 行权价(场内期权适用)
     */
    private BigDecimal strike;

    /**
     * 期权类型
     */

    private int optionType;

    /**
     * 是否是主力合约
     */
    private MainContractEnum mainContract;

    /**
     * 股息率
     */
    private BigDecimal dividendYield;

    /**
     * 波动率偏移量
     */
    private BigDecimal volOffset;

    @ApiModelProperty(value = "风险预警")
    /**
     * 风险预警 0:否(默认) 1:是
     */
    private int isRiskWarning;

}
