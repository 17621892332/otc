package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
public class TradeCodeQueryDTO {


    @ApiModelProperty("交易编号列表")
    List<String> tradeCodeList;
}
