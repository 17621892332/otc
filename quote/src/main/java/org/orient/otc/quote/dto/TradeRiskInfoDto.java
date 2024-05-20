package org.orient.otc.quote.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("风险dto")
public class TradeRiskInfoDto extends BasePage {

    @ApiModelProperty("结算日期")
    private LocalDate settlementDate;

    @ApiModelProperty("交易编号")
    private String tradeCode;

    @ApiModelProperty("交易对手方ID")
    private List<Integer> clientIds;

    @ApiModelProperty("簿记账户ID")
    private Set<Integer> assetIds;

    @ApiModelProperty("簿记账户组ID")
    private Set<Integer> assetGroupIds;

    @ApiModelProperty("期权类型")
    private Set<String> optionTypes;
    @ApiModelProperty("期权组合类型")
    private Set<String> optionCombTypes;

/*    @ApiModelProperty("标的合约")
    private String underlyingCode;*/

    @ApiModelProperty(value = "标的合约")
    private List<String> underlyingCodeList;

    @ApiModelProperty("场内or场外,[exchange:场内,over:场外]")
    private String tradeRiskCacularResultSourceType;
    /**
     * 是否存续交易
     * true  过滤存续数量>0
     * 反之不过滤
     */
    @ApiModelProperty("是否存续交易")
    Boolean availableTrade;

}
