package org.orient.otc.quote.dto.confirmbook;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dzrh
 */
@Data
public class DownloadTradeConfirmBookDTO {
    @ApiModelProperty("交易ID")
    private List<Integer> tradeIdList;
}
