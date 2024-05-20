package org.orient.otc.quote.dto.settlementbook;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/** 生成结算确认书dto
 * @author dzrh
 */
@Data
public class DownloadSettlementConfirmBookDTO {
    @ApiModelProperty("平仓ID")
    private List<Integer>  closeIdList;
}
