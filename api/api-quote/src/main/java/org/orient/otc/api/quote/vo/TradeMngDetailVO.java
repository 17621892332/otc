package org.orient.otc.api.quote.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class TradeMngDetailVO implements Serializable {
   /**
    * 交易记录
    */
   @ApiModelProperty(value = "交易记录")
   List<TradeMngVO> tradeMngVOList;
   /**
    * 资金记录
    */
   @ApiModelProperty(value = "资金记录")
   List<CapitalRecordsVO> capitalRecordsVOList;
   /**
    * 平仓记录
    */
   @ApiModelProperty(value = "平仓记录")
   List<TradeCloseMngVO> tradeCloseMngVOList;
}
