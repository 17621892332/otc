package org.orient.otc.netty.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.CallOrPutEnum;

import java.util.List;

/**
 * @author dzrh
 */
@Data
@ApiModel
public class RiskInfoQueryDto {
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
     * 产业链
     */
    @ApiModelProperty(value = "产业链")
    private  List<String> varietyTypeList;

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

    @ApiModelProperty(value = "交易员列表")
    private List<String> traderList;

    /**
     * 是否过滤已平仓数据
     */
    @ApiModelProperty(value = "是否过滤已平仓数据")
    private  Boolean isNotClose;

    /**
     * 看涨看跌
     */
    @ApiModelProperty(value = "看涨看跌")
    private CallOrPutEnum callOrPut;
}
