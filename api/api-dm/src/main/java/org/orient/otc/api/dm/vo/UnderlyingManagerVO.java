package org.orient.otc.api.dm.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.orient.otc.api.dm.enums.MainContractEnum;
import org.orient.otc.api.dm.enums.UnderlyingState;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
@Schema
public class UnderlyingManagerVO {
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
     * 品种代码
     */
    @ApiModelProperty(value = "品种代码")
    private String varietyCode;

    /**
     * 品种名称
     */
    @ApiModelProperty(value = "品种名称")
    private String varietyName;

    /**
     * 资产类型
     */
    @ApiModelProperty(value = "资产类型")
    private String underlyingAssetType;
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
     * 推送报价单位
     */
    @ApiModelProperty(value = "推送报价单位")
    private String unit;
    /**
     * 合约乘数
     */
    @ApiModelProperty(value = "合约乘数")
    private Integer contractSize;

    /**
     * 交易所代码
     */
    @ApiModelProperty(value = "交易所代码")
    private String exchange;

    /**
     * 上市日期,格式: yyyy-MM-dd
     */
    @ApiModelProperty(value = "上市日期,格式: yyyy-MM-dd")
    private LocalDate createDate;
    /**
     * 到期日期,格式: yyyy-MM-dd
     */
    @ApiModelProperty(value = "到期日期,格式: yyyy-MM-dd")
    private LocalDate expireDate;

    /**
     * 行权价(场内期权适用)
     */
    @ApiModelProperty(value = "行权价(场内期权适用)")
    private BigDecimal strike;

    /**
     * 期权类型
     */
    @ApiModelProperty(value = "期权类型")
    private int optionType;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用： 0启用 1禁用")
    private Integer enabled;

    @ApiModelProperty(value = "状态code")
    private UnderlyingState underlyingState;

    @ApiModelProperty(value = "状态")
    private String underlyingStateName;

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

    /**
     * 风险预警 0:否(默认) 1:是
     */
    private int isRiskWarning;
}
