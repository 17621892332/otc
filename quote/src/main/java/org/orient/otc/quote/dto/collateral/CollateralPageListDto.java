package org.orient.otc.quote.dto.collateral;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.CollateralEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("抵押品dto")
public class CollateralPageListDto extends BasePage {

    @ApiModelProperty("抵押时间-开始")
    private LocalDate startCollateralDate;

    @ApiModelProperty("抵押时间-结束")
    private LocalDate endCollateralDate;


    @ApiModelProperty("赎回时间-开始")
    private LocalDate startRedemptionDate;

    @ApiModelProperty("赎回时间-结束")
    private LocalDate endRedemptionDate;

    @ApiModelProperty(value = "客户id数组")
    private List<Integer> clientIdList;

    @ApiModelProperty(value = "抵押状态数组")
    private List<CollateralEnum.CollateralStatusEnum> collateralStatusList;

    @ApiModelProperty(value = "执行状态数组")
    private List<CollateralEnum.ExecuteStatusEnum> executeStatusList;

    @ApiModelProperty(value = "抵押品名称数组")
    private List<Integer> varietyIdList;

}
