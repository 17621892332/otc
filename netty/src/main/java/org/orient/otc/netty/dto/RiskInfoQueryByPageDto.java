package org.orient.otc.netty.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.orient.otc.api.quote.enums.CallOrPutEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.util.List;

/**
 * 情景分析中风险交易分页查询dto , 为了不影响其他方法的入参校验
 * 此dto和RiskInfoQueryDto基本一致 , 除了分页参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ApiModel
public class RiskInfoQueryByPageDto extends BasePage {
    /**
     * 簿记ID列表
     */
    @ApiModelProperty("簿记ID列表")
    List<Integer> assetIdList;

    /**
     * 客户ID列表
     */
    @ApiModelProperty(value = "客户ID")
    private  List<Integer> clientIdList;

    /**
     * 合约代码列表
     */
    @ApiModelProperty(value = "合约代码列表")
    private  List<String> underlyingCodeList;

    /**
     * 选中的合约代码列表
     */
    @ApiModelProperty(value = "选中的合约代码列表")
    private  List<String> selectedUnderlyingCodeList;

    @ApiModelProperty(value = "选中的交易编号列表")
    private  String tradeCode;
    @ApiModelProperty(value = "选中的交易类型列表")
    private  List<String> tradeTypeList;
    @ApiModelProperty(value = "选中的标的品种列表")
    private  List<String> varietyCodeList;

    /**
     * 看涨看跌
     */
    @ApiModelProperty(value = "看涨看跌")
    private CallOrPutEnum callOrPut;
}
