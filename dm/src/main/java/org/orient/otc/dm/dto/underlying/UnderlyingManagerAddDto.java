package org.orient.otc.dm.dto.underlying;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.database.enums.EnabledEnum;
import org.orient.otc.api.dm.enums.MainContractEnum;
import org.orient.otc.api.dm.enums.UnderlyingState;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 品种信息(VarietyType)表实体类
 *
 * @author makejava
 * @since 2023-07-14 11:18:49
 */
@Data
@ApiModel
public class UnderlyingManagerAddDto {


    /**
     * 标的资产码
     */
    @ApiModelProperty(value = "标的资产码")
    @NotEmpty(message = "标的资产码不能为空")
    private String underlyingCode;

    /**
     * 标的资产码
     */
    @ApiModelProperty(value = "场内标的资产码")
    @NotEmpty(message = "场内标的资产码不能为空")
    private String exchangeUnderlyingCode;

    /**
     * 标的名称
     */
    @ApiModelProperty(value = "标的名称")
    @NotEmpty(message = "标的名称不能为空")
    private String underlyingName;

    /**
     * 品种id
     */
    @ApiModelProperty(value = "品种id")
    @NotNull(message = "品种不能为空")
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
    private EnabledEnum enabled;

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
    @NotNull(message = "主力合约不能为空")
    private MainContractEnum mainContract;

    /**
     * 股息率
     */
    private BigDecimal dividendYield;

    /**
     * 波动率偏移量
     */
    private BigDecimal volOffset;
    /**
     * 风险预警 0:否(默认) 1:是
     */
    private int isRiskWarning;

    }

